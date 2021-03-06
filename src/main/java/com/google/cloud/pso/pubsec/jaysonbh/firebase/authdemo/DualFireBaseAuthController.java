package com.google.cloud.pso.pubsec.jaysonbh.firebase.authdemo;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.ExportedUserRecord;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.ListUsersPage;
import java.io.FileInputStream;

import java.io.IOException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DualFireBaseAuthController {
  private final String SA_CLOUD_SCE_0 = "PATH/TO-KEY-1.json";
  private final String SECONDARY_PROJECT_ID="PROJECT-2-NAME";

  FirebaseApp optionalApp0;
  FirebaseApp optionalApp1;

  //INITIALIZE TWO SEPARATE FIREBASE/IDENTITY PLATFORM APPS
  public DualFireBaseAuthController() throws IOException {
    FileInputStream serviceAccount0 = new FileInputStream(SA_CLOUD_SCE_0);
    FileInputStream serviceAccount1 = new FileInputStream(SA_CLOUD_SCE_0);

    FirebaseOptions firebaseAppOptions0 = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount0))
        .build();

    FirebaseOptions firebaseAppOptions1 = FirebaseOptions.builder()
        .setProjectId(SECONDARY_PROJECT_ID)
        .setCredentials(GoogleCredentials.fromStream(serviceAccount1))
        .build();

    optionalApp0 = FirebaseApp.initializeApp(firebaseAppOptions0, "Auth-App-0");
    optionalApp1 = FirebaseApp.initializeApp(firebaseAppOptions1, "Auth-App-1");

    System.out.println("Initialized Project 0 Auth App: " + optionalApp0.getName());
    System.out.println("Initialized Project 1 Auth App: " + optionalApp1.getName());
  }

  @GetMapping("/")
  public String hello() throws FirebaseAuthException {
    printAllFirebaseAuthUsers(optionalApp0, optionalApp0.getName());
    printAllFirebaseAuthUsers(optionalApp1, optionalApp1.getName());

    return "Hello, World!";
  }

  @PostMapping("/vToken")
  public String vToken(@RequestParam String token) throws FirebaseAuthException {
    // optionalApp0
    FirebaseToken firebaseToken = null;

    try {
      firebaseToken = FirebaseAuth.getInstance(optionalApp0).verifyIdToken(token);
    }
    catch (Error error) {
      System.err.println("Error Verifying token! ");
      System.err.println("Received Error: " + error);
    }

    if (firebaseToken != null) {
      String userEmail = firebaseToken.getEmail();
      System.out.println("User Email from Token: " + userEmail);

      return "Token Verified Successfully as user: " + userEmail + "\n";
    }

    return "checked token";
  }

  @PostMapping("/vToken1")
  public String vToken1(@RequestParam String token) throws FirebaseAuthException {
    // optionalApp1
    FirebaseToken firebaseToken1 = FirebaseAuth.getInstance(optionalApp1).verifyIdToken(token);
    try {
      firebaseToken1 = FirebaseAuth.getInstance(optionalApp1).verifyIdToken(token);
    }
    catch (Error error) {
      System.err.println("Error Verifying token! ");
      System.err.println("Received Error: " + error);
    }
    if (firebaseToken1 != null){
      String userEmail = firebaseToken1.getEmail();
      System.out.println("User Email from Token: " + userEmail);

      return "Token Verified Successfully as user: " + userEmail + "\n";

    }

    return "checked token";
  }

  public void printAllFirebaseAuthUsers(FirebaseApp fbApp, String appName) throws FirebaseAuthException {

    System.out.println("Printing All Users from App: " + appName);

    ListUsersPage page = FirebaseAuth.getInstance(fbApp).listUsers(null, 5);
    while (page != null) {
      for(ExportedUserRecord user : page.getValues()) {
        System.out.println("User: " + user.getUid());
      }
      page = page.getNextPage();
    }
  }

}
