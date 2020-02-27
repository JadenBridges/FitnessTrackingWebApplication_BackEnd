package com.example.FitnessTracker;

import org.hibernate.dialect.Database;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class GroupFeedController
{
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;

    private DatabaseUtility databaseUtility = new DatabaseUtility();

    //---------------------------------------------------------------
    // Method:  getPosts
    // Purpose: To return all posts in a group feed.
    // Inputs:  groupID
    // Output:  arraylist of posts
    //---------------------------------------------------------------
    @GetMapping("/groupfeed/get")
    public ArrayList<Post> getPosts(@RequestParam(name="groupID") int groupID) {
        // get all posts
        ArrayList<Post> posts = (ArrayList<Post>)postRepository.findAll();
        ArrayList<Group> groups = (ArrayList<Group>)groupRepository.findAll();
        Group correctGroup = new Group();

        // get the correct group
        for(Group group : groups)
        {
            if(group.getGroupID() == groupID) {
                correctGroup = group;
            }
        }

        // get users from group
        ArrayList<User> users = (ArrayList<User>)correctGroup.getUsers();
        boolean partOfGroup;

        // remove posts that are not associated with the users of the group
        // for each post, find
        for (Post post : posts) {
            partOfGroup = false;
            for(User user : users) {
                if (post.getActivity().getUserID() == user.getUserID()) {
                    partOfGroup = true;
                }
            }
            if (!partOfGroup) {
                posts.remove(post);
            }
        }
        // return only posts for the group
        return posts;
    }

    //---------------------------------------------------------------
    // Method:  updatePostLikes
    // Purpose: To update the number of likes for a specified post,
    //          specifically by increasing by 1.
    // Inputs:  postID
    // Output:  int representing new number of likes for the post
    //          (-1 if post doesn't exist)
    //---------------------------------------------------------------
    @PutMapping("/groupfeed/like-post")
    public int updatePostLikes(@RequestParam(name="postID") int postID) {
        int likes = -1;

        Post post = databaseUtility.getPostById(postID);

        // if the post exists
        if(post != null) {
            // increment the number of its likes
            post.setLikes(post.getLikes()+1);
            // update the post entry in the database with the new number of likes
            postRepository.save(post);
            // get the new number of likes
            likes = post.getLikes();
        }

        return likes;
    }

    //---------------------------------------------------------------
    // Method:  createPostComment
    // Purpose: To add a comment to the specified post.
    // Inputs:  new comment
    // Output:  int (1 for successful delete, 0 for failure to delete)
    //---------------------------------------------------------------
    @PostMapping("/groupfeed/comment-post")
    public void createPostComment(@RequestBody Comment comment) {
        commentRepository.save(comment);
    }

    //---------------------------------------------------------------
    // Method:  deletePost
    // Purpose: To delete a post from a group feed if the
    //          userID passed in matches that of the post.
    // Inputs:  postID and userID
    // Output:  int (1 for successful delete, 0 for failure to delete)
    //---------------------------------------------------------------
    @DeleteMapping("/groupfeed/delete-post")
    public int deletePost(@RequestParam(name="postID") int postID, @RequestParam(name="userID") int userID) {
        int is_deleted = 0;

        Post post = databaseUtility.getPostById(postID);

        // if the post exists and the userID matches that of the userID passed in
        if((post != null) && (post.getActivity().getUserID() == userID)) {
            // delete the post
            postRepository.delete(post);
            is_deleted = 1;
        }

        return is_deleted;
    }

    //---------------------------------------------------------------
    // Method:  addUserToGroup
    // Purpose: To add a specific user to a specific group
    // Inputs:  userID and groupID
    // Output:  int (1 for successful delete, 0 for failure to delete)
    //---------------------------------------------------------------
    @PostMapping("/groupfeed/adduser")
    public void addUserToGroup(@RequestParam(name="userID") int userID, @RequestParam(name="groupID") int groupID) {
        // get user from userID
        User user = databaseUtility.getUserById(userID);

        // get group from groupID
        Group group = databaseUtility.getGroupById(groupID);

        // add user to group
        ArrayList<User> users = (ArrayList<User>)group.getUsers();
        users.add(user);
        group.setUsers(users);
    }

    //---------------------------------------------------------------
    // Method:  removeUserFromGroup
    // Purpose: To remove a specific user from a specific group
    // Inputs:  userID and groupID
    // Output:  boolean (true for successful delete, false for failure to delete)
    //---------------------------------------------------------------
    @PutMapping("/groupfeed/removeuser")
    public boolean removeUserFromGroup(@RequestParam(name="userID") int userID, @RequestParam(name="groupID") int groupID) {
        boolean userDeleted = false;

        // get user from userID
        User user = databaseUtility.getUserById(userID);

        // get group from groupID
        Group group = databaseUtility.getGroupById(groupID);

        // remove user from group
        ArrayList<User> users = (ArrayList<User>)group.getUsers();
        userDeleted = users.remove(user);
        group.setUsers(users);

        return userDeleted;
    }
}