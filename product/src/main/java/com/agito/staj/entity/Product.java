package com.agito.staj.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.springframework.context.i18n.LocaleContextHolder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Entity
@Table(name = "product")
public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "product_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int productId;

    @Column(nullable = false, unique = true)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    private Double price;

    @Column(name = "comm_completed")
    private boolean commCompleted;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<ProductTranslation> translations = new ArrayList<>();

    public Product() {
    }

    public Product(String code, String name, Category category, Double price, boolean commCompleted) {
        this.code = code;
        this.category = category;
        this.price = price;
        this.commCompleted = commCompleted;
        this.setName(name);
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public boolean isCommCompleted() {
        return commCompleted;
    }

    public void setCommCompleted(boolean commCompleted) {
        this.commCompleted = commCompleted;
    }

    public List<ProductTranslation> getTranslations() {
        return translations;
    }

    public void setTranslations(List<ProductTranslation> translations) {
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
                .map(ProductTranslation::getName)
                .findFirst()
                .orElseGet(() -> translations.stream()
                        .filter(t -> t.getLanguageCode() != null && t.getLanguageCode().equalsIgnoreCase(defaultLang))
                        .map(ProductTranslation::getName)
                        .findFirst()
                        .orElseGet(() -> translations.get(0).getName()));
    }

    public void addOrUpdateTranslation(String lang, String name) {
        if (translations == null) {
            translations = new ArrayList<>();
        }
        for (ProductTranslation pt : translations) {
            if (pt.getLanguageCode() != null && pt.getLanguageCode().equalsIgnoreCase(lang)) {
                pt.setName(name);
                return;
            }
        }
        translations.add(new ProductTranslation(this, lang, name));
    }
}
