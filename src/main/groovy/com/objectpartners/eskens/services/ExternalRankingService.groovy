package com.objectpartners.eskens.services

import com.objectpartners.eskens.entities.Person
import com.objectpartners.eskens.model.Rank
import org.springframework.stereotype.Service
import org.springframework.validation.annotation.Validated

/**
 * This is to mimic calls to an external 3rd party service that you wouldn't want to test locally.
 */
@Service
class ExternalRankingService {

    @SuppressWarnings("GrMethodMayBeStatic")
    Rank getRank(Person person) {
        throw new RuntimeException('This feature is not yet implemented.')
    }
}
