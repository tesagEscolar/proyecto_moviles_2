package com.example.gamescrud.ui.settings;

import android.content.Intent;
import android.credentials.CredentialManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.gamescrud.R;
import com.example.gamescrud.databinding.FragmentSettingsBinding;
import com.example.gamescrud.models.FireBaseGameContract;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.w3c.dom.Text;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private FireBaseGameContract gameContract;
    FirebaseAuth auth;
    TextView textView;
    Button btn;
    ImageView pp;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SettingsViewModel settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);

        gameContract = new FireBaseGameContract();


        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("920075751803-p6vt4e9mn62u3o44b5els1o2s79rmebo.apps.googleusercontent.com")
                .requestEmail()
                .build();

        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(getActivity(), googleSignInOptions);



        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        textView = binding.textSettings;
        btn = binding.SigninBtn;
        pp = binding.circularImageView;



        btn.setOnClickListener(v -> {
            Intent intent = googleSignInClient.getSignInIntent();
            // Start activity for result
            startActivityForResult(intent, 100);
        });

        auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        if(firebaseUser != null) {
            textView.setText("User: " + firebaseUser.getDisplayName());
        }


        settingsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check condition
        if (requestCode == 100) {

            Task<GoogleSignInAccount> signInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            // check condition
            if (signInAccountTask.isSuccessful()) {
                // When google sign in successful initialize string
                String s = "Google sign in successful";
                // Display Toast
                Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();

                try {
                    GoogleSignInAccount googleSignInAccount = signInAccountTask.getResult(ApiException.class);
                        if (googleSignInAccount != null) {
                            AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
                            auth.signInWithCredential(authCredential).addOnCompleteListener(getActivity(), (OnCompleteListener<AuthResult>) task -> {
                                // Check condition
                                if (task.isSuccessful()) {
                                    // When task is successful redirect to profile activity display Toast
                                    textView.setText(auth.getCurrentUser().getDisplayName());
                                    Glide.with(pp.getContext()).load(auth.getCurrentUser().getPhotoUrl().toString()).placeholder(R.drawable.avatar_1).error(R.drawable.avatar_1).diskCacheStrategy(DiskCacheStrategy.ALL).into(pp);
                                    btn.setVisibility(View.INVISIBLE);

                                    //Test this pls
                                    gameContract.setPath(auth.getCurrentUser().getUid());
                                    Toast.makeText(getContext(), "Auth ✅: " + auth.getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
                                } else {
                                    // When task is unsuccessful display Toast
                                    Toast.makeText(getContext(), "Firebase Auth failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } catch (ApiException e) {
                    throw new RuntimeException(e);
                }

            }
            else{
                Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}