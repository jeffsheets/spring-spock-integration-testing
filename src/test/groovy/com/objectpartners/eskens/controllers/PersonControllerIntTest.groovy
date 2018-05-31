package com.objectpartners.eskens.controllers

import com.objectpartners.eskens.model.Rank
import com.objectpartners.eskens.services.ExternalRankingService
import com.objectpartners.eskens.services.ValidatedExternalRankingService
import org.spockframework.spring.SpringBean
import org.spockframework.spring.SpringSpy
import org.spockframework.spring.UnwrapAopProxy
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import spock.lang.Specification

import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * An integration test illustrating how to wire everything w/ Spring,
 * but replace certain components with Spock mocks
 * Created by derek on 4/10/17.
 */
@SpringBootTest
@AutoConfigureMockMvc
class PersonControllerIntTest extends Specification {

    @Autowired MockMvc mvc

    /**
     * SpringSpy will wrap the Spring injected Service with a Spy
     * UnwrapAopProxy will remove the cglib @Validated proxy annotated inside ExternalRankingService
     * However it will not use a cached test config, so many tests could be slow.
     *   see PersonControllerIntCachedTest for how to use the spring cached context config
     */
    @SpringSpy
    @UnwrapAopProxy
    ValidatedExternalRankingService validatedExternalRankingService

    /**
     * SpringBean will put the mock into the spring context
     */
    @SpringBean
    ExternalRankingService externalRankingService = Mock()

    def "GetRank"() {
        when: 'Calling getRank for a known seed data entity'
        MvcResult mvcResult = mvc.perform(get("/persons/1/rank").contentType(APPLICATION_JSON))
                                .andExpect(status().is2xxSuccessful()).andReturn()

        then: 'we define the mock for JUST the external service'
        1 * externalRankingService.getRank(_) >> {
            new Rank(level: 1, classification: 'Captain')
        }
        noExceptionThrown()

        when: 'inspecting the contents'
        def resultingJson = mvcResult.response.contentAsString

        then: 'the result contains a mix of mocked service data and actual wired component data'
        resultingJson == 'Capt James Kirk ~ Captain:Level 1'
    }

    def "GetValidatedRank"() {
        when: 'Calling getRank for a known seed data entity'
        MvcResult mvcResult = mvc.perform(get("/persons/1/validatedRank").contentType(APPLICATION_JSON))
                                .andExpect(status().is2xxSuccessful()).andReturn()

        then: 'we define the mock for the external service'
        1 * validatedExternalRankingService.getRank(_) >> {
            new Rank(level: 1, classification: 'Captain')
        }
        noExceptionThrown()

        when: 'inspecting the contents'
        def resultingJson = mvcResult.response.contentAsString

        then: 'the result contains a mix of mocked service data and actual wired component data'
        resultingJson == 'Capt James Kirk ~ Captain:Level 1'
    }
}
