package com.example.mircea.instaapp.Firebase;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mircea.instaapp.Activities.MainActivity;
import com.example.mircea.instaapp.HelperClasses.EmailRefactor;
import com.example.mircea.instaapp.Raw.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class SignUpUserWithEmail {

    private static final String TAG = "TAG MESSAGE";
    private User tempUser;
    private Uri pathToImage;

    public SignUpUserWithEmail(User tempUser, Uri pathToImage){

        this.tempUser = tempUser;
        this.pathToImage = pathToImage;

    }
    public void firebaseSignUpWithEmail(FirebaseAuth mAuth, EditText emailTextField, EditText passwordTextField,
                                        EditText usernameTextField, EditText confEmailTextField, Bitmap imageBitmap,
                                        Context context){

        mAuth.createUserWithEmailAndPassword(emailTextField.getText().toString(), passwordTextField.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        FirebaseUser user = mAuth.getCurrentUser();

                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(usernameTextField.getText().toString())
                                .build();

                        user.updateProfile(profileUpdates);

                        //userPhotoUrl = null;

                        EmailRefactor emRef = new EmailRefactor();

                        String email = emRef.refactorEmail(emailTextField.getText().toString());

                        tempUser = new User(email, usernameTextField.getText().toString(), 0, 0);

                        if(imageBitmap != null && pathToImage != null){

                            String imageUrl = getImageUrl(emailTextField.getText().toString(), pathToImage);
                            StorageReference storageReference = FirebaseStorage.getInstance().getReference("ProfilePictures").child(imageUrl);

                            UploadTask uploadTask = storageReference.putFile(pathToImage);

                            try{

                                uploadUsernameToFirebase(uploadTask, mAuth, tempUser, usernameTextField, emailTextField);

                            }catch(NullPointerException ex){

                                ex.printStackTrace();
                                clearTextFields(emailTextField, confEmailTextField, passwordTextField, usernameTextField);

                                Toast.makeText(context, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
                            }
                        }

                        addUserInDatabase(tempUser, emailTextField);
                        context.startActivity(new Intent(context, MainActivity.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        /*catch all the possible exceptions*/
                        //TODO(1): This needs more work
                        try{

                            throw e;

                        }catch (FirebaseAuthWeakPasswordException ex){
                            Toast.makeText(context, "The password you chose is to weak, please try again", Toast.LENGTH_SHORT).show();

                        }catch (FirebaseAuthUserCollisionException ex){

                            Toast.makeText(context, "Account already in use", Toast.LENGTH_SHORT).show();

                        }catch (Exception ex){
                            Log.i(TAG, ex.getMessage());
                        }
                    }
                });
    }

    public void uploadUsernameToFirebase(UploadTask uploadTask, FirebaseAuth mAuth, User tempUser, EditText usernameTextField, EditText emailTextField){
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                String userPhotoUrl = taskSnapshot.getDownloadUrl().toString();
                FirebaseUser usez = mAuth.getCurrentUser();

                UserProfileChangeRequest profileUpdatez = new UserProfileChangeRequest.Builder()
                        .setDisplayName(usernameTextField.getText().toString())
                        .setPhotoUri(taskSnapshot.getDownloadUrl())
                        .build();

                usez.updateProfile(profileUpdatez);

                tempUser.setProfilePicture(userPhotoUrl);

                addUserInDatabase(tempUser, emailTextField);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                e.printStackTrace();
            }
        });
    }

    private void addUserInDatabase(User temp_user, EditText emailTextField) {
        /*Add the user in the realtime database*/

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Users");
        EmailRefactor emRef = new EmailRefactor();
        String em = emRef.refactorEmail(emailTextField.getText().toString());

        databaseRef.child(em).setValue(temp_user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    Log.d(TAG, "SUCCESS");
                }
            }
        });
    }

    private void clearTextFields(EditText... textFields){

        for(EditText aux:textFields){
            aux.setText("");
        }

    }

    @NonNull
    private String getImageUrl(String em, Uri pathToImage) {

        return em + "*" + pathToImage.getLastPathSegment();
    }
}
