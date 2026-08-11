package com.agito.staj.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.io.Serializable;

@Entity
@Data
public class Category implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

    // Many children can map to One parent
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    // One parent can map to Many children.
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Category> children = new ArrayList<>();

    /**
     *
     * @return the hierarchy path of the category.
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
}