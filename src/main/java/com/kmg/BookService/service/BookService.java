package com.kmg.BookService.service;

import com.kmg.BookService.dto.AladinItem;
import com.kmg.BookService.model.Book;
import com.kmg.BookService.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final OpenApiClient openApiClient;

    @Transactional(readOnly = true)
    public List<Book> getAllBooks(){
        return bookRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Book> getBookByIsbn(String isbn){
        return bookRepository.findByIsbn(isbn);
    }

    /**
     * 알라딘 검색 결과를 한꺼번에 저장
     */
    @Transactional
    public List<Book> searchAndSaveBooks(String keyword) { // 반환 타입이 List<Book>으로 변경됨
        List<AladinItem> items = openApiClient.search(keyword);
        
        if (items == null || items.isEmpty()) {
            log.warn("검색 결과가 없습니다: {}", keyword);
            return Collections.emptyList();
        }

        // 1. DTO 리스트를 Entity 리스트로 변환 (중복 제외 로직 포함)
        List<Book> booksToSave = items.stream()
                .map(item -> {
                    // ISBN13 우선, 없으면 일반 ISBN 사용
                    String isbnValue = (item.getIsbn13() != null && !item.getIsbn13().isEmpty()) 
                                        ? item.getIsbn13() : item.getIsbn();
                    
                    // 이미 존재하는 책인지 확인 (Optional 활용)
                    if (bookRepository.findByIsbn(isbnValue).isPresent()) {
                        return null; // 이미 있으면 null 반환하여 나중에 필터링
                    }

                    return Book.builder()
                            .title(item.getTitle())
                            .isbn(isbnValue)
                            .author(item.getAuthor())
                            .publisher(item.getPublisher())
                            .pubDate(item.getPubDate())
                            .description(item.getDescription())
                            .build();
                })
                .filter(java.util.Objects::nonNull) // 이미 존재하는 책(null) 제외
                .collect(Collectors.toList());

        // 2. 새로운 책이 있다면 한꺼번에 저장
        if (!booksToSave.isEmpty()) {
            log.info("{}권의 새로운 책을 저장합니다.", booksToSave.size());
            return bookRepository.saveAll(booksToSave);
        }

        log.info("새로 저장할 책이 없습니다.");
        return Collections.emptyList();
    }

    @Transactional
    public Book saveBook(Book book){
        return bookRepository.save(book);
    }
}