package com.example.praktika;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProjectStorage {
    private static final String PREF_NAME = "projects_state";
    private static final String KEY_PROJECTS = "projects_json";

    public static void saveProject(Context context, String type, String name, String startDate,
                                   String endDate, String projectFor, String source, String category) {
        List<ProjectItem> projects = getProjects(context);
        projects.add(0, new ProjectItem(
                UUID.randomUUID().toString(),
                type,
                name,
                startDate,
                endDate,
                projectFor,
                source,
                category,
                System.currentTimeMillis()
        ));
        persist(context, projects);
    }

    public static List<ProjectItem> getProjects(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_PROJECTS, "[]");
        List<ProjectItem> result = new ArrayList<>();

        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                ProjectItem item = new ProjectItem(
                        obj.optString("id", ""),
                        obj.optString("type", ""),
                        obj.optString("name", ""),
                        obj.optString("startDate", ""),
                        obj.optString("endDate", ""),
                        obj.optString("projectFor", ""),
                        obj.optString("source", ""),
                        obj.optString("category", ""),
                        obj.optLong("createdAt", 0)
                );
                result.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private static void persist(Context context, List<ProjectItem> projects) {
        try {
            JSONArray array = new JSONArray();
            for (ProjectItem project : projects) {
                JSONObject obj = new JSONObject();
                obj.put("id", project.getId());
                obj.put("type", project.getType());
                obj.put("name", project.getName());
                obj.put("startDate", project.getStartDate());
                obj.put("endDate", project.getEndDate());
                obj.put("projectFor", project.getProjectFor());
                obj.put("source", project.getSource());
                obj.put("category", project.getCategory());
                obj.put("createdAt", project.getCreatedAt());
                array.put(obj);
            }

            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                    .edit()
                    .putString(KEY_PROJECTS, array.toString())
                    .apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class ProjectItem {
        private final String id;
        private final String type;
        private final String name;
        private final String startDate;
        private final String endDate;
        private final String projectFor;
        private final String source;
        private final String category;
        private final long createdAt;

        public ProjectItem(String id, String type, String name, String startDate, String endDate,
                           String projectFor, String source, String category, long createdAt) {
            this.id = id;
            this.type = type;
            this.name = name;
            this.startDate = startDate;
            this.endDate = endDate;
            this.projectFor = projectFor;
            this.source = source;
            this.category = category;
            this.createdAt = createdAt;
        }

        public String getId() { return id; }
        public String getType() { return type; }
        public String getName() { return name; }
        public String getStartDate() { return startDate; }
        public String getEndDate() { return endDate; }
        public String getProjectFor() { return projectFor; }
        public String getSource() { return source; }
        public String getCategory() { return category; }
        public long getCreatedAt() { return createdAt; }
    }
}