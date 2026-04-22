package com.example.praktika;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.praktika.ProjectStorage.ProjectItem;
import java.util.List;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ViewHolder> {
    private List<ProjectItem> projects;
    private OnProjectClickListener listener;

    public interface OnProjectClickListener {
        void onProjectClick(ProjectItem project);
    }

    public ProjectAdapter(List<ProjectItem> projects, OnProjectClickListener listener) {
        this.projects = projects;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_project, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProjectItem project = projects.get(position);
        holder.tvName.setText(project.getName());
        holder.tvType.setText(project.getType());

        long days = (System.currentTimeMillis() - project.getCreatedAt()) / (1000 * 60 * 60 * 24);
        if (days <= 0) {
            holder.tvMeta.setText("Создан сегодня");
        } else if (days == 1) {
            holder.tvMeta.setText("Создан 1 день назад");
        } else {
            holder.tvMeta.setText("Создан " + days + " дн. назад");
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onProjectClick(project);
        });
        holder.btnOpen.setOnClickListener(v -> {
            if (listener != null) listener.onProjectClick(project);
        });
    }

    @Override
    public int getItemCount() {
        return projects == null ? 0 : projects.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvType, tvMeta;
        Button btnOpen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvProjectName);
            tvType = itemView.findViewById(R.id.tvProjectType);
            tvMeta = itemView.findViewById(R.id.tvProjectMeta);
            btnOpen = itemView.findViewById(R.id.btnOpenProject);
        }
    }
}