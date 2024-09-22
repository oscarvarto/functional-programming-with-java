package mx.oscarvarto;

import fj.data.NonEmptyList;
import fj.data.Validation;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import static fj.Semigroup.nonEmptyListSemigroup;
import static fj.data.Validation.condition;
import static mx.oscarvarto.ValidationConstants.*;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class ValidationTest {

    private static <E, T> Validation<NonEmptyList<E>, T> check(final boolean c, final E e, final T t) {
        return condition(c, e, t).nel();
    }

    @Test
    void validationRulesTest1() {
        var errors = new PersonValidator("  ", -5).validate().fail().map(ErrorMsg::msg);
        assertThat(errors).containsExactly(NAME_EMPTY_OR_WHITESPACE_ERROR_MSG, NEGATIVE_AGE_ERROR_MSG);
    }

    @Test
    void validationRulesTest2() {
        var validatedPerson = new PersonValidator("Chabelo", 340).validate();
        assertThat(validatedPerson.fail().map(ErrorMsg::msg)).containsExactly(MAX_AGE_ERROR_MSG);
    }

    @Test
    void validationRulesTest3() {
        var validatedPerson = new PersonValidator("Luke Skywalker", 32).validate();
        assertThat(validatedPerson.isSuccess()).isTrue();
        var person = validatedPerson.successE("Wrong validation");
        assertThat(person).isEqualTo(new Person("Luke Skywalker", 32));
    }

    record PersonValidator(String name, int age) {
        public Validation<NonEmptyList<ErrorMsg>, Person> validate() {
            // Validation rules
            var validatedName = check(!name.isBlank(),
                    NAME_EMPTY_OR_WHITESPACE_ERROR_MSG,
                    name
            );

            var validatedMinAge = check(age >= 0,
                    NEGATIVE_AGE_ERROR_MSG,
                    age
            );

            var validatedMaxAge = check(age <= MAX_AGE,
                    MAX_AGE_ERROR_MSG,
                    age
            );

            return validatedName.accumulate(
                            nonEmptyListSemigroup(),
                            validatedMinAge,
                            validatedMaxAge,
                            (n, a1, a2) -> new Person(n, a1)
                    ).f()
                    .map(nes -> nes.map(ErrorMsg::new));
        }
    }

    record Person(String name, int age) {}

    record ErrorMsg(String msg) {}
}
