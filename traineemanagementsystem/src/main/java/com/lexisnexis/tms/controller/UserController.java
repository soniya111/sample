package com.lexisnexis.tms.controller;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lexisnexis.tms.dto.LoginDto;

import com.lexisnexis.tms.entity.User;
import com.lexisnexis.tms.entity.UserLogin;
import com.lexisnexis.tms.entity.WorkHistory;
import com.lexisnexis.tms.exception.UserNotFoundException;
import com.lexisnexis.tms.repository.LoginTableRepository;
import com.lexisnexis.tms.repository.UserRepository;
import com.lexisnexis.tms.response.APIResponse;
import com.lexisnexis.tms.service.LoginService;
import com.lexisnexis.tms.service.PdfService;
import com.lexisnexis.tms.service.UserPdfExporter;
import com.lexisnexis.tms.service.UserService;
import com.lexisnexis.tms.util.PasswEncrypt;
import com.lowagie.text.DocumentException;

@RestController
@RequestMapping("/tms/api/v1")
public class UserController {

	@Autowired
	UserService userService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	PasswEncrypt passwEncrypt;

	// Register api
	@PostMapping("/register")
	public String registerNewUser(@RequestBody @Valid User user) throws NoSuchAlgorithmException {

		user.setPassword(passwEncrypt.encryptPass(user.getPassword()));
		userService.registerNewUser(user);
		return "user registration successfully";
	}

	@PostMapping("/workHistory")
	public String saveWorkHistory(@RequestBody WorkHistory workHistory) {

		userService.updateWorkHistory(workHistory);
		return "work history updated";

	}

	@GetMapping("/getUser")
	public User getUserDetail(String userName) throws UserNotFoundException {
		User user = userService.getUserDetails(userName);
		return user;
	}

//	@GetMapping("/getDataByUserName/{userName}")
//	public ResponseEntity<User> getData(@PathVariable String userName) throws UserNotFoundException {
//		return new ResponseEntity<>(UserService.getDataByUserName(userName), HttpStatus.OK);
//	}

	@GetMapping("/getAllUsers")
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}
	
	@Autowired
	LoginService loginService;
	
	@Autowired
	UserLogin loginTable;
	
	
	
	@Autowired
	LoginTableRepository loginTableRepo;
	
	@PostMapping("/login")
	public ResponseEntity<APIResponse> login(@RequestBody LoginDto loginDto) throws InterruptedException, NoSuchAlgorithmException{
		APIResponse apiResponse= loginService.login(loginDto);

		return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
	}

	@Autowired
	PdfService pdfService;

	@GetMapping("/users/report")
	public void createpdf(HttpServletResponse response) throws DocumentException, IOException {

		response.setContentType("application/pdf");
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		String currentDateTime = dateFormatter.format(new Date());

		String headerKey = "Content-Disposition";

		String headerValue = "attachment; filename=logWork_" + currentDateTime + ".pdf";

		response.setHeader(headerKey, headerValue);
		List<WorkHistory> l = pdfService.getAll();
		UserPdfExporter userPdfExporter = new UserPdfExporter(l);
		userPdfExporter.export(response);
	}
	
	
	
	
}
