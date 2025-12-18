package org.yearup.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProfileDao;
import org.yearup.data.UserDao;
import org.yearup.models.Profile;
import org.yearup.models.User;

import java.security.Principal;

@RestController
@RequestMapping("/profile")
@PreAuthorize("isAuthenticated")
@CrossOrigin
public class ProfileController {
    private final ProfileDao profileDao;
    private final UserDao userDao;

    public ProfileController(ProfileDao profileDao, UserDao userDao) {
        this.profileDao = profileDao;
        this.userDao = userDao;
    }


   @GetMapping
   public Profile getProfile(Principal principal) {
        User user = getCurrentUser(principal);
        Profile profile = profileDao.getUserById(user.getId());
        if (profile == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return profile;
   }

   @PutMapping
   public Profile updateProfile(@RequestBody Profile profile, Principal principal) {
        User user = getCurrentUser(principal);

        profileDao.update(user.getId(), profile);

        return profileDao.getUserById(user.getId());

   }

   private User getCurrentUser(Principal principal) {
        String username = principal.getName();
        User user = userDao.getByUserName(username);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username not found");
        }
        return user;
   }
}
