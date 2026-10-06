package com.example.gson.model;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class Customer {
    @SerializedName("customer_id")
    private final long id;
    private final String name;
    private final String email;
    private final LocalDate birthDate;
    private final List<String> interests;
    private final Money creditLimit;
    private final String nickname;
    private transient String internalNote;

    public Customer(long id, String name, String email, LocalDate birthDate,
                    List<String> interests, Money creditLimit, String nickname) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "name");
        this.email = Objects.requireNonNull(email, "email");
        this.birthDate = Objects.requireNonNull(birthDate, "birthDate");
        this.interests = new ArrayList<String>(Objects.requireNonNull(interests, "interests"));
        this.creditLimit = Objects.requireNonNull(creditLimit, "creditLimit");
        this.nickname = nickname;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public List<String> getInterests() {
        return Collections.unmodifiableList(interests);
    }

    public Money getCreditLimit() {
        return creditLimit;
    }

    public String getNickname() {
        return nickname;
    }

    public String getInternalNote() {
        return internalNote;
    }

    public void setInternalNote(String internalNote) {
        this.internalNote = internalNote;
    }
}
