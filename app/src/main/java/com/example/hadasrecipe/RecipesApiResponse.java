package com.example.hadasrecipe;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class RecipesApiResponse {

    @JsonProperty("hits")
    public List<Hit> hits;

    public static class Hit {
        @JsonProperty("recipe")
        public ApiRecipe recipe;
    }

    public static class ApiRecipe {
        @JsonProperty("url")
        public String url;

        @JsonProperty("image")
        public String image;

        public String getImage() {
            return image;
        }

        @JsonProperty("label")
        public String label;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }
}
