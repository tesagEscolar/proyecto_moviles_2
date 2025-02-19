package com.example.gamescrud.ui.transform;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.gamescrud.R;
import com.example.gamescrud.databinding.FragmentTransformBinding;
import com.example.gamescrud.databinding.ItemTransformBinding;
import com.example.gamescrud.models.FireBaseGameContract;
import com.example.gamescrud.models.GameModel;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Fragment that demonstrates a responsive layout pattern where the format of the content
 * transforms depending on the size of the screen. Specifically this Fragment shows items in
 * the [RecyclerView] using LinearLayoutManager in a small screen
 * and shows items using GridLayoutManager in a large screen.
 */
public class TransformFragment extends Fragment {

    private FragmentTransformBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        TransformViewModel transformViewModel =
                new ViewModelProvider(this).get(TransformViewModel.class);

        binding = FragmentTransformBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = binding.recyclerviewTransform;
        ListAdapter<GameModel, TransformViewHolder> adapter = new TransformAdapter();
        recyclerView.setAdapter(adapter);
        transformViewModel.getTexts().observe(getViewLifecycleOwner(), adapter::submitList);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private static class TransformAdapter extends ListAdapter<GameModel, TransformViewHolder> {

        protected TransformAdapter() {
            super(new DiffUtil.ItemCallback<GameModel>() {
                @Override
                public boolean areItemsTheSame(@NonNull GameModel oldItem, @NonNull GameModel newItem) {
                    return oldItem.getId().equals(newItem.getId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull GameModel oldItem, @NonNull GameModel newItem) {
                    return oldItem.getId().equals(newItem.getId());
                }
            });
        }

        @NonNull
        @Override
        public TransformViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemTransformBinding binding = ItemTransformBinding.inflate(LayoutInflater.from(parent.getContext()));
            return new TransformViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull TransformViewHolder holder, int position) {
            GameModel game = getItem(position);
            holder.textView.setText(game.getName());

            FireBaseGameContract fc = new FireBaseGameContract();
            try {
                    fc.getImage(game.getImagePath()).getDownloadUrl().addOnSuccessListener(uri -> {
                    Glide.with(holder.imageView.getContext()).load(uri.toString()).placeholder(R.drawable.avatar_1).error(R.drawable.avatar_1).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.imageView);
                });

            } catch (IOException e) {
                throw new RuntimeException(e);
            }


            holder.itemView.setOnClickListener(view -> {
               final Bundle bundle = new Bundle();
               bundle.putParcelable("game", game);
               Navigation.findNavController(view).navigate(R.id.nav_reflow, bundle);
               Snackbar.make(view, game.getName() + ": " + game.getId(), Snackbar.LENGTH_LONG).show();
           });
        }
    }

    private static class TransformViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;
        private final TextView textView;

        public TransformViewHolder(ItemTransformBinding binding) {
            super(binding.getRoot());
            imageView = binding.circularImageView;
            textView = binding.textViewItemTransform;
        }
    }
}