package com.example.gamescrud.ui.reflow;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.gamescrud.R;
import com.example.gamescrud.databinding.FragmentReflowBinding;
import com.example.gamescrud.models.FireBaseGameContract;
import com.example.gamescrud.models.GameModel;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.Calendar;


public class ReflowFragment extends Fragment {

    private FragmentReflowBinding binding;
    private FireBaseGameContract gameContract;
    private ActivityResultLauncher<Intent> launcher;

    ImageView gameImage;
    TextView releaseDate;
    EditText gameName;
    EditText gameDesc;
    EditText gameTrophies;
    EditText gameCategory;
    EditText gameBestAward;
    Uri imageUri;


    GameModel game2 = new GameModel();

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ReflowViewModel reflowViewModel =
                new ViewModelProvider(this).get(ReflowViewModel.class);


        binding = FragmentReflowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        gameContract = new FireBaseGameContract();
        gameImage = binding.circularImageView;

        gameName = binding.gameName;
        gameDesc = binding.gameDesc;
        releaseDate = binding.releaseDate;
        gameTrophies = binding.gameTrophies;
        gameCategory= binding.gameCategory;
        gameBestAward= binding.gameBestAward;


        final Button updateBtn = binding.BtnUpdateGame;
        final Button deleteBtn = binding.BtnDeleteGame;
        gameImage.setOnClickListener(view -> onImagePress());
        final MaterialDatePicker<Long> mdp =  MaterialDatePicker.Builder.datePicker()
                .setTitleText("Games Release Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setPositiveButtonText("Submit")
                .build();

        mdp.addOnPositiveButtonClickListener(view -> releaseDate.setText(mdp.getHeaderText()));
        releaseDate.setOnClickListener(view -> mdp.show(getParentFragmentManager(), "MAT_DATE_PICKER"));


        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            imageUri = data.getData();
                            Bitmap selectedImageBitmap = null;
                            try {
                                selectedImageBitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(requireActivity().getContentResolver(), imageUri));
                            }
                            catch (IOException e) {
                                e.printStackTrace();
                            }
                            gameImage.setImageBitmap(selectedImageBitmap);
                        }
                    }
                });



        if(getArguments() == null){
            gameImage.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.avatar_1, null));
            updateBtn.setText("Create");
            deleteBtn.setEnabled(false);
        }else if(getArguments().containsKey("image")){
            Bitmap image = getArguments().getParcelable("image");
            gameImage.setImageBitmap(image);
            imageUri = getArguments().getParcelable("imageUri");

            if(getArguments().containsKey("lat")){
                gameBestAward.setText(getArguments().getString("lat") + " , " + getArguments().getString("long"));
            }
            updateBtn.setText("Create");
            deleteBtn.setEnabled(false);

        } else{
            game2 = getArguments().getParcelable("game");
            updateBtn.setText("Update");

            FireBaseGameContract fc = new FireBaseGameContract();
            try {
                fc.getImage(game2.getImagePath()).getDownloadUrl().addOnSuccessListener(uri -> {
                    imageUri = uri;
                    Glide
                        .with(this)
                        .load(uri.toString())
                        .placeholder(R.drawable.avatar_1)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(gameImage);

                });

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            gameName.setText(game2.getName());
            gameDesc.setText(game2.getGameDesc());
            gameTrophies.setText(game2.getGameTrophies());
            gameCategory.setText(game2.getGameCategory());
            gameBestAward.setText(game2.getGameBestAward());
            releaseDate.setText(game2.getReleaseDate());
        }

        updateBtn.setOnClickListener(this::onUpdate);
        deleteBtn.setOnClickListener(this::onDelete);

//        reflowViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void onUpdate(View v){

        game2.setName(gameName.getText().toString());
        game2.setGameTrophies(gameTrophies.getText().toString());
        game2.setGameDesc(gameDesc.getText().toString());
        game2.setGameCategory(gameCategory.getText().toString());
        game2.setGameBestAward(gameBestAward.getText().toString());
        game2.setReleaseDate(releaseDate.getText().toString());

        if(getArguments() !=null && !getArguments().containsKey("image")){

            this.gameContract.updateGame(game2, imageUri, objects -> {
                Navigation.findNavController(v).popBackStack();
                Snackbar.make(v, "Landscape updated", Snackbar.LENGTH_LONG).show();
            });

        }else{
            this.gameContract.createGame(game2, imageUri, objects -> {
                Navigation.findNavController(v).navigate(R.id.action_nav_reflow_to_transform);
                Snackbar.make(v, "Landscape created", Snackbar.LENGTH_LONG).show();
            });
        }
    }

    public void onDelete(View v) {
        assert getArguments() != null;
        this.gameContract.deleteGame(game2.getId(), unused -> {
            Navigation.findNavController(v).popBackStack();
            Snackbar.make(v, "Landscape deleted", Snackbar.LENGTH_LONG).show();
        });
    }

    public void onImagePress(){
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        launcher.launch(i);
    }

}