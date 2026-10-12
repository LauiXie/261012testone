package com.example.idcard.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IdCardValidatorTest {

    @Test
    void acceptsValidIdNumber() {
        assertTrue(IdCardValidator.isValidFormat("110101199003070011"));
        // 末位小写 x 同样接受
        assertTrue(IdCardValidator.isValidFormat("44010619881123003x"));
    }

    @Test
    void rejectsInvalidIdNumber() {
        assertFalse(IdCardValidator.isValidFormat(null));
        assertFalse(IdCardValidator.isValidFormat(""));
        assertFalse(IdCardValidator.isValidFormat("11010119900307001"));
        assertFalse(IdCardValidator.isValidFormat("11010119900307001X1"));
        assertFalse(IdCardValidator.isValidFormat("010101199003070011"));
        assertFalse(IdCardValidator.isValidFormat("110101199013070011"));
    }

    @Test
    void derivesGenderFromSequenceDigit() {
        assertEquals("男", IdCardValidator.deriveGender("110101199003070011"));
        assertEquals("女", IdCardValidator.deriveGender("310101199508120026"));
        assertNull(IdCardValidator.deriveGender("not-an-id"));
    }

    @Test
    void derivesBirthDate() {
        assertEquals(LocalDate.of(1990, 3, 7), IdCardValidator.deriveBirthDate("110101199003070011"));
        // 2 月 31 日：格式匹配但日期非法
        assertNull(IdCardValidator.deriveBirthDate("110101199002310010"));
    }

    @Test
    void normalizesCaseAndWhitespace() {
        assertEquals("44010619881123003X", IdCardValidator.normalize(" 44010619881123003x "));
        assertNull(IdCardValidator.normalize(null));
    }
}
