package com.objectpartners.eskens.controllers

import com.objectpartners.eskens.model.Rank
import com.objectpartners.eskens.services.PersonService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = '/persons')
@SuppressWarnings("GroovyUnusedDeclaration")
class PersonController {

    final PersonService personService

    PersonController(PersonService personService) {
        this.personService = personService
    }

    @GetMapping(path = '{id}/rank')
    String getNameAndRank(@PathVariable(name = 'id') Long id ) {
        def name = personService.getAddressToForPersonId(id)
        Rank rank = personService.getRank(id)
        return "$name ~ $rank.classification:Level $rank.level"
    }

    @GetMapping(path = '{id}/validatedRank')
    String getNameAndValidatedRank(@PathVariable(name = 'id') Long id ) {
        def name = personService.getAddressToForPersonId(id)
        Rank rank = personService.getValidatedRank(id)
        return "$name ~ $rank.classification:Level $rank.level"
    }
}
