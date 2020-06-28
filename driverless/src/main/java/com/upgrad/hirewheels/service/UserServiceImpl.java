package com.upgrad.hirewheels.service;

import com.upgrad.hirewheels.dao.UserRoleRepository;
import com.upgrad.hirewheels.dto.AddUserDTO;
import com.upgrad.hirewheels.entities.Users;
import com.upgrad.hirewheels.dao.UserRepository;
import com.upgrad.hirewheels.exceptions.APIException;
import com.upgrad.hirewheels.exceptions.UserAlreadyExistsException;
import com.upgrad.hirewheels.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl implements UserService{

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserRoleRepository userRoleRepository;

    /**
     * Checks if the user is registered or not. If registered it returns the user details else throws an error.
     * @param email
     * @param password
     * @return logged in user details.
     */

    public Users getUserDetails(String email, String password) {
            Users checkUser = userRepository.findByEmail(email);
            if (checkUser == null){
                throw new UserNotFoundException("User Not Registered");
            }
            Users user = userRepository.findByEmailAndPassword(email, password);
            return user;
    }

    /**
     * Checks if the user mobile number/email is already exists or not. If not exists, saves the user detail else throws an error.
     * @param user
     * @return saved user details.
     */

    public Users createUser(AddUserDTO user) {
            Users returnedUser = userRepository.findByEmail(user.getEmail());
                if ( returnedUser != null) {
                    throw new UserAlreadyExistsException("Email Already Exists");
                }
            Users returnedUser1 = userRepository.findByMobileNo(user.getMobileNo());
            if (returnedUser1 != null) {
                throw new UserAlreadyExistsException("Mobile Number Already Exists");
                }
            Users users = new Users();
            users.setWalletMoney(10000);
            users.setUserRole(userRoleRepository.findByRoleId(2));
            users.setEmail(user.getEmail());
            users.setPassword(user.getPassword());
            users.setFirstName(user.getFirstName());
            users.setLastName(user.getLastName());
            users.setMobileNo(user.getMobileNo());
            Users savedUser = userRepository.save(users);
            return savedUser;
    }

    /**
     * Checks if the user is registered or not. If registered it checks the new password is not equal to the current password.
     * If the password os different, it updates the password else throws an error.
     * @param email
     * @param mobileNo
     * @param password
     * @return
     */

    public Boolean updatePassword(String email, long mobileNo, String password) {
            Users user = userRepository.findByEmailAndMobileNo(email,mobileNo);
            if(user == null){
                throw new APIException("Invalid Email/Mobile Number");
            }
            String currentPassword = user.getPassword();
                if(user != null && !currentPassword.equals(password)) {
                    user.setPassword(password);
                    userRepository.save(user);
                    return true;
                } else {
                    throw new APIException("The new password should be different from the existing one");
                }
    }

}
