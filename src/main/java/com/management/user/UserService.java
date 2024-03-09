package com.management.user;

import com.management.auth.EmailService;
import com.management.exception.UnauthorizedOperationException;
import com.management.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final EmailService emailService;

    private final PasswordEncoder passwordEncoder;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy");

    public void createUser(UserRequest request){
        User user = new User(request.getFirstname(), request.getLastname(), request.getEmail());
        String randomPassword = UUID.randomUUID().toString();
        user.setPassword(passwordEncoder.encode(randomPassword));
        user.setRole(Role.USER);
        user.setTempPassword(true);
        userRepository.save(user);
        String body = "you account is created and here are your login credentials: \n" +
                "email: "+user.getEmail()+" \n" +
                "password: "+randomPassword+" \n";
        emailService.sendEmail(user.getEmail(), "reset account password", body);
    }

    public void updateUser(UserDTO userDTO, Principal connectedUser){

        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        if(!user.getEmail().equals(userDTO.getEmail()) && Role.USER.equals(user.getRole())){
            throw new UnauthorizedOperationException("Cannot update another user profile");
        }

        var userFromDb = userRepository.findByEmail(userDTO.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));



        userFromDb.setFirstname(userDTO.getFirstname());
        userFromDb.setLastname(userDTO.getLastname());
        userFromDb.setBirthdate(userDTO.getBirthdate() != null ? LocalDate.parse(userDTO.getBirthdate(), formatter): null);
        userFromDb.setGender(userDTO.getGender() != null ? GenderEnum.valueOf(userDTO.getGender()): null);
        if(Role.ADMIN.equals(user.getRole())){
            userFromDb.setRole(userDTO.getRole() != null ? Role.valueOf(userDTO.getRole()): null);
        }
        userRepository.save(userFromDb);
    }

    public void deleteUser(Integer id){

        var user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        userRepository.delete(user);
    }

    public UserDTO findUser(Integer id){

        var user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return convertToDTO(user);
    }

    public List<UserDTO> findUsers(String firstname, String lastname, List<Integer> userIds, String role, Principal connectedUser){

        Set<User> users = new HashSet<>();
        if(firstname != null){
            users.addAll(userRepository.findUsersByFirstnameContainingIgnoreCase(firstname).orElse(new ArrayList<>()));
        }

        if(lastname != null){
            users.addAll(userRepository.findUsersByLastnameContainingIgnoreCase(lastname).orElse(new ArrayList<>()));
        }

        if(!CollectionUtils.isEmpty(userIds)){
            users.addAll(userRepository.findUsersByIdIn(userIds).orElse(new ArrayList<>()));
        }

        var userPrincipal = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        if(role != null && Role.ADMIN.equals(userPrincipal.getRole())){
            users.addAll(userRepository.findUsersByRole(Role.valueOf(role)).orElse(new ArrayList<>()));
        }else if(role != null && Role.USER.equals(userPrincipal.getRole())){
            throw new UnauthorizedOperationException("Only admin user can retrieve user by role");
        }

        if(users.isEmpty() && firstname == null && lastname == null && userIds == null && role == null){
            users.addAll(userRepository.findAll());
        }


        // Convert User objects to UserDTO objects
        List<UserDTO> userDTOs = new ArrayList<>();
        for (User user : users) {
            // Assuming there's a method to convert User to UserDTO
            UserDTO userDTO = convertToDTO(user);
            userDTOs.add(userDTO);
        }

        return userDTOs;
    }

    private UserDTO convertToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setFirstname(user.getFirstname());
        userDTO.setLastname(user.getLastname());
        userDTO.setEmail(user.getEmail());
        userDTO.setBirthdate(user.getBirthdate() != null ? user.getBirthdate().toString(): null);
        userDTO.setGender(user.getGender() != null ? user.getGender().name(): null);
        userDTO.setRole(user.getRole() != null ? user.getRole().name(): null);

        return userDTO;
    }

}
