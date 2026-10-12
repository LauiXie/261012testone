package com.example.idcard.repository;

import com.example.idcard.entity.IdCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface IdCardRepository extends JpaRepository<IdCard, Long> {

    boolean existsByIdNumber(String idNumber);

    boolean existsByIdNumberAndIdNot(String idNumber, Long id);

    /** 按姓名或身份证号模糊分页查询。 */
    Page<IdCard> findByNameContainingOrIdNumberContaining(String name, String idNumber, Pageable pageable);

    long countByGender(String gender);

    /** 统计用：取出所有非空出生日期。 */
    @Query("select c.birthDate from IdCard c where c.birthDate is not null")
    List<LocalDate> findAllBirthDates();
}
