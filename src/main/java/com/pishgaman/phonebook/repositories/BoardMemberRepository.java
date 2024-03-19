package com.pishgaman.phonebook.repositories;

import com.pishgaman.phonebook.entities.BoardMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardMemberRepository extends JpaRepository<BoardMember, Long> {
}