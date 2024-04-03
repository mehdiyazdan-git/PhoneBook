package com.pishgaman.phonebook.repositories;

import com.pishgaman.phonebook.entities.BoardMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardMemberRepository extends JpaRepository<BoardMember, Long> {
    @Query("select b from BoardMember b where b.company.id = :companyId")
    List<BoardMember> findAllByCompanyId(@Param("companyId") Long companyId);

    @Query("select b from BoardMember b where b.company.id = :company_id and b.position.id = :position_id")
    BoardMember findByCompanyIdAndPositionId(@Param("company_id") Long company_id, @Param("position_id") Long position_id);

    @Query("""
            select b from BoardMember b
            where b.person.id = :personId and b.company.id = :companyId and b.position.id = :positionId""")
    BoardMember findByPersonIdAndCompanyIdAndPositionId(@Param("personId") Long personId, @Param("companyId") Long companyId, @Param("positionId") Long positionId);


    @Query("select b from BoardMember b where b.company.id = :company_id and b.position.id = :position_id and b.id <> :boardMemberId")
    BoardMember findByCompanyIdAndPositionIdAndNotBoardMemberId(@Param("company_id") Long companyId, @Param("position_id") Long positionId, @Param("boardMemberId") Long boardMemberId);

    @Query("""
            select b from BoardMember b
            where b.person.id = :personId and b.company.id = :companyId and b.position.id = :positionId and b.id <> :boardMemberId""")
    BoardMember findByPersonIdAndCompanyIdAndPositionIdAndNotBoardMemberId(@Param("personId") Long personId, @Param("companyId") Long companyId, @Param("positionId") Long positionId, @Param("boardMemberId") Long boardMemberId);
}