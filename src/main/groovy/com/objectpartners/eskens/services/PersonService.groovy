package com.objectpartners.eskens.services

import com.objectpartners.eskens.entities.Person
import com.objectpartners.eskens.model.Rank
import com.objectpartners.eskens.repos.PersonRepo
import org.springframework.stereotype.Service

@Service
class PersonService {

    final PersonRepo personRepo

    final ExternalRankingService externalRankingService

    final ValidatedExternalRankingService validatedExternalRankingService

    PersonService(PersonRepo pr, ExternalRankingService ers, ValidatedExternalRankingService vers) {
        this.personRepo = pr
        this.externalRankingService = ers
        this.validatedExternalRankingService = vers
    }

    String getAddressToForPersonId(Long personId) {
        def p = getPerson(personId)
        "$p.title $p.firstName $p.lastName"
    }

    Rank getRank(Long personId) {
        externalRankingService.getRank(getPerson(personId))
    }

    Rank getValidatedRank(Long personId) {
        validatedExternalRankingService.getRank(getPerson(personId))
    }

    private Person getPerson(Long personId) {
        personRepo.findOne(personId)
    }
}
