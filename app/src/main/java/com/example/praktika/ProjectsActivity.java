package com.example.praktika;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.praktika.ProjectStorage.ProjectItem;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ProjectsActivity extends AppCompatActivity {

    private LinearLayout projectsContainer;
    private TextView emptyProjectsText;
    private TextView btnAddProject;

    // Навигация
    private LinearLayout navHome, navCatalog, navProjects, navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projects);

        // Инициализация View
        projectsContainer = findViewById(R.id.projectsContainer);
        emptyProjectsText = findViewById(R.id.tvEmptyProjects);
        btnAddProject = findViewById(R.id.btnAddProject);

        // Навигация
        navHome = findViewById(R.id.navHome);
        navCatalog = findViewById(R.id.navCatalog);
        navProjects = findViewById(R.id.navProjects);
        navProfile = findViewById(R.id.navProfile);

        // Кнопка создания проекта
        btnAddProject.setOnClickListener(v ->
                startActivity(new Intent(this, CreateProjectActivity.class)));

        // Нижняя навигация
        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        // Главная
        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(ProjectsActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        // Каталог
        navCatalog.setOnClickListener(v -> {
            Intent intent = new Intent(ProjectsActivity.this, CatalogActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        // Проекты (текущий экран)
        navProjects.setOnClickListener(v -> {
            // Уже в проектах
        });

        // Профиль
        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ProjectsActivity.this, MainActivity7.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindProjects();
    }

    private void bindProjects() {
        if (projectsContainer == null) return;

        List<ProjectItem> projects = ProjectStorage.getProjects(this);
        projectsContainer.removeAllViews();

        if (projects.isEmpty()) {
            emptyProjectsText.setVisibility(View.VISIBLE);
            projectsContainer.setVisibility(View.GONE);
            return;
        }

        emptyProjectsText.setVisibility(View.GONE);
        projectsContainer.setVisibility(View.VISIBLE);
        LayoutInflater inflater = LayoutInflater.from(this);

        for (ProjectItem project : projects) {
            View card = inflater.inflate(R.layout.item_project, projectsContainer, false);
            TextView name = card.findViewById(R.id.tvProjectName);
            TextView type = card.findViewById(R.id.tvProjectType);
            TextView meta = card.findViewById(R.id.tvProjectMeta);
            Button open = card.findViewById(R.id.btnOpenProject);

            name.setText(project.getName());
            type.setText(project.getType());
            meta.setText(buildMeta(project));

            View.OnClickListener listener = v -> showProjectDetails(project);
            card.setOnClickListener(listener);
            open.setOnClickListener(listener);

            projectsContainer.addView(card);
        }
    }

    private String buildMeta(ProjectItem project) {
        long elapsedDays = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - project.getCreatedAt());
        if (elapsedDays <= 0) {
            return "Создан сегодня";
        }
        if (elapsedDays == 1) {
            return "Создан 1 день назад";
        }
        return "Создан " + elapsedDays + " дн. назад";
    }

    private void showProjectDetails(ProjectItem project) {
        String details = "Тип: " + project.getType() + "\n" +
                "Дата начала: " + project.getStartDate() + "\n" +
                "Дата окончания: " + project.getEndDate() + "\n" +
                "Кому: " + project.getProjectFor() + "\n" +
                "Источник: " + project.getSource() + "\n" +
                "Категория: " + project.getCategory();
        android.widget.Toast.makeText(this, details, android.widget.Toast.LENGTH_LONG).show();
    }
}