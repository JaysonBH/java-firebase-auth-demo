package com.google.cloud.pso.pubsec.jaysonbh.firebase.authdemo;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.ExportedUserRecord;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.ListUsersPage;
import java.io.FileInputStream;

import java.io.IOException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DualFireBaseAuthController {
  private final String SA_CLOUD_SCE_0 = "PATH/TO-KEY-1.json";
  private final String SA_CLOUD_SCE_1 = "PATH/TO-KEY-1.json";

  FirebaseApp optionalApp0;
  FirebaseApp optionalApp1;

  //INITIALIZE TWO SEPARATE FIREBASE/IDENTITY PLATFORM APPS
  public DualFireBaseAuthController() throws IOException {
    FileInputStream serviceAccount0 = new FileInputStream(SA_CLOUD_SCE_0);
    FileInputStream serviceAccount1 = new FileInputStream(SA_CLOUD_SCE_1);

    FirebaseOptions firebaseAppOptions0 = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount0))
        .build();

    FirebaseOptions firebaseAppOptions1 = FirebaseOptions.builder()
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
