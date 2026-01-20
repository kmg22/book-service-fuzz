package com.kmg.BookService.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="books")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String isbn;

    private String author;
    private String publisher;
    private String pubDate;

    @Column(length = 1000)
    private String description;
}
