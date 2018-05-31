package com.objectpartners.eskens.services

import com.objectpartners.eskens.entities.Person
import com.objectpartners.eskens.model.Rank
import org.springframework.stereotype.Service
import org.springframework.validation.annotation.Validated

/**
 * This class is @Validated, which makes it a spring proxied service
 *   so unwrapping is necessary to use a Mock
 */
@Service
@Validated
class ValidatedExternalRankingService {

    @SuppressWarnings("GrMethodMayBeStatic")
    Rank getRank(Person person) {
        throw new RuntimeException('This feature is not yet implemented.')
    }
}
