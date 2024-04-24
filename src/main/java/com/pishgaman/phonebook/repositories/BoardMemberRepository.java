package com.pishgaman.phonebook.repositories;

import com.pishgaman.phonebook.entities.BoardMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardMemberRepository extends JpaRepository<BoardMember, Long>, JpaSpecificationExecutor<BoardMember> {
    @Query("select b from BoardMember b where b.company.id = :companyId")
    List<BoardMember> findAllByCompanyId(@Param("companyId") Long companyId);

    @Query("select b from BoardMember b where b.person.id = :person_id")
    List<BoardMember> findAllByPersonId(@Param("person_id") Long person_id);

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

    @Query("""
            select (count(b) > 0) from BoardMember b
            where b.createdBy = :createdBy or b.lastModifiedBy = :lastModifiedBy""")
    boolean existsByCreatedByOrLastModifiedBy(@Param("createdBy") int createdBy, @Param("lastModifiedBy") int lastModifiedBy);

    @Query("select (count(b) > 0) from BoardMember b where b.company.id = :companyId")
    boolean existsByCompanyId(@Param("companyId") Long companyId);

    @Query("select (count(b) > 0) from BoardMember b where b.person.id = :personId")
    boolean existsByPersonId(@Param("personId") Long personId);

    @Query("select (count(b) > 0) from BoardMember b where b.position.id = :positionId")
    boolean existsByPositionId(@Param("positionId") Long positionId);
}