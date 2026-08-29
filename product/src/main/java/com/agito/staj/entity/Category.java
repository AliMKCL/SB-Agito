package com.agito.staj.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.springframework.context.i18n.LocaleContextHolder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Entity
@Table(name = "category")
public class Category implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Many children can map to One parent
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    // One parent can map to Many children.
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Category> children = new ArrayList<>();

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<CategoryTranslation> translations = new ArrayList<>();

    public Category() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }

    public List<Category> getChildren() {
        return children;
    }

    public void setChildren(List<Category> children) {
        this.children = children;
    }

    public List<CategoryTranslation> getTranslations() {
        return translations;
    }

    public void setTranslations(List<CategoryTranslation> translations) {
        this.translations = translations;
    }

    public String getName() {
        Locale currentLocale = LocaleContextHolder.getLocale();
        String activeLang = currentLocale != null ? currentLocale.getLanguage() : "en";
        return getNameForLocale(activeLang, "en");
    }

    public void setName(String name) {
        if (name == null) {
            return;
        }
        Locale currentLocale = LocaleContextHolder.getLocale();
        String activeLang = currentLocale != null ? currentLocale.getLanguage() : "en";
        addOrUpdateTranslation(activeLang, name);
    }

    public String getNameForLocale(String lang, String defaultLang) {
        if (translations == null || translations.isEmpty()) {
            return null;
        }
        return translations.stream()
                .filter(t -> t.getLanguageCode() != null && t.getLanguageCode().equalsIgnoreCase(lang))
                .map(CategoryTranslation::getName)
                .findFirst()
                .orElseGet(() -> translations.stream()
                        .filter(t -> t.getLanguageCode() != null && t.getLanguageCode().equalsIgnoreCase(defaultLang))
                        .map(CategoryTranslation::getName)
                        .findFirst()
                        .orElseGet(() -> translations.get(0).getName()));
    }

    public void addOrUpdateTranslation(String lang, String name) {
        if (translations == null) {
            translations = new ArrayList<>();
        }
        for (CategoryTranslation ct : translations) {
            if (ct.getLanguageCode() != null && ct.getLanguageCode().equalsIgnoreCase(lang)) {
                ct.setName(name);
                return;
            }
        }
        translations.add(new CategoryTranslation(this, lang, name));
    }

    /**
     * @return the localized hierarchy path of the category.
     */
    public String getCategoryPath() {
        List<String> path = new ArrayList<>();
        Category current = this;
        while (current != null) {
            path.add(current.getName());
            current = current.getParent();
        }
        Collections.reverse(path);
        return String.join(" --> ", path);
    }

    public String getCategoryPath(String lang) {
        List<String> path = new ArrayList<>();
        Category current = this;
        while (current != null) {
            path.add(current.getNameForLocale(lang, "en"));
            current = current.getParent();
        }
        Collections.reverse(path);
        return String.join(" --> ", path);
    }
}
