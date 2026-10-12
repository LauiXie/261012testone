package com.example.idcard.service;

import com.example.idcard.common.BusinessException;
import com.example.idcard.common.ErrorCode;
import com.example.idcard.dto.IdCardRequest;
import com.example.idcard.dto.IdCardResponse;
import com.example.idcard.dto.StatsResponse;
import com.example.idcard.entity.IdCard;
import com.example.idcard.repository.IdCardRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IdCardServiceTest {

    @Mock
    private IdCardRepository repository;

    @InjectMocks
    private IdCardService service;

    private IdCardRequest request(String name, String idNumber, String gender, LocalDate birthDate) {
        return new IdCardRequest(name, idNumber, gender, birthDate, "北京市东城区景山前街4号");
    }

    @Test
    void createSavesEntityAndDerivesGenderAndBirthDate() {
        when(repository.existsByIdNumber("110101199003070011")).thenReturn(false);
        when(repository.save(any(IdCard.class))).thenAnswer(invocation -> {
            IdCard entity = invocation.getArgument(0);
            entity.setId(1L);
            return entity;
        });

        IdCardResponse response = service.create(request("张伟", " 110101199003070011 ", null, null));

        assertEquals(1L, response.id());
        assertEquals("张伟", response.name());
        assertEquals("男", response.gender());
        assertEquals(LocalDate.of(1990, 3, 7), response.birthDate());
        verify(repository).save(any(IdCard.class));
    }

    @Test
    void createRejectsInvalidFormat() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.create(request("张伟", "12345", null, null)));

        assertEquals(ErrorCode.BAD_REQUEST, ex.getCode());
        verify(repository, never()).save(any(IdCard.class));
    }

    @Test
    void createRejectsDuplicatedIdNumber() {
        when(repository.existsByIdNumber("110101199003070011")).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.create(request("张伟", "110101199003070011", "男", LocalDate.of(1990, 3, 7))));

        assertEquals(ErrorCode.CONFLICT, ex.getCode());
        verify(repository, never()).save(any(IdCard.class));
    }

    @Test
    void createRejectsUnsupportedGender() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.create(request("张伟", "110101199003070011", "male", null)));

        assertEquals(ErrorCode.BAD_REQUEST, ex.getCode());
    }

    @Test
    void getThrowsWhenRecordMissing() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () -> service.get(99L));

        assertEquals(ErrorCode.NOT_FOUND, ex.getCode());
    }

    @Test
    void updateRejectsIdNumberUsedByAnotherRecord() {
        IdCard existing = new IdCard();
        existing.setId(2L);
        existing.setName("王芳");
        existing.setIdNumber("310101199508120026");
        when(repository.findById(2L)).thenReturn(Optional.of(existing));
        when(repository.existsByIdNumberAndIdNot("110101199003070011", 2L)).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () -> service.update(2L,
                request("王芳", "110101199003070011", "女", LocalDate.of(1995, 8, 12))));

        assertEquals(ErrorCode.CONFLICT, ex.getCode());
    }

    @Test
    void deleteRemovesExistingRecord() {
        IdCard existing = new IdCard();
        existing.setId(3L);
        when(repository.findById(3L)).thenReturn(Optional.of(existing));

        service.delete(3L);

        verify(repository).delete(existing);
    }

    @Test
    void statsComputesCountsRatiosAndAgeGroups() {
        when(repository.count()).thenReturn(4L);
        when(repository.countByGender("男")).thenReturn(1L);
        when(repository.countByGender("女")).thenReturn(2L);
        when(repository.findAllBirthDates()).thenReturn(List.of(
                LocalDate.now().minusYears(10),
                LocalDate.now().minusYears(25),
                LocalDate.now().minusYears(40)));

        StatsResponse stats = service.stats();

        assertEquals(4L, stats.total());
        assertEquals(1L, stats.maleCount());
        assertEquals(2L, stats.femaleCount());
        assertEquals(1L, stats.otherCount());
        assertEquals(new BigDecimal("0.2500"), stats.maleRatio());
        assertEquals(new BigDecimal("0.5000"), stats.femaleRatio());
        assertEquals(5, stats.ageGroups().size());
        assertEquals(1L, stats.ageGroups().get(0).count());
        assertEquals(1L, stats.ageGroups().get(1).count());
        assertEquals(1L, stats.ageGroups().get(2).count());
    }
}
