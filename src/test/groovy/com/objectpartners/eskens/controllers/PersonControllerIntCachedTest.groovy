package com.objectpartners.eskens.controllers

import com.objectpartners.eskens.config.IntegrationTestMockingConfig
import com.objectpartners.eskens.services.ExternalRankingService
import com.objectpartners.eskens.services.Rank
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.util.AopTestUtils
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import spock.lang.Specification

import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * An integration test illustrating how to wire everything w/ Spring,
 * but replace certain components with Spock mocks
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import([IntegrationTestMockingConfig])  //uses a cached spring context for speed
class PersonControllerIntCachedTest extends Specification {

    @Autowired MockMvc mvc

    @Autowired
    ExternalRankingService proxiedExternalRankingService

    ExternalRankingService externalRankingService

    void setup() {
        /**
         * the Validated proxied ExternalRankingService must be unwrapped to use the Mock
         *
         *   see PersonControllerIntTest to see how to make this easier with @SpringSpy @UnwrapAopProxy
         */
        externalRankingService = AopTestUtils.getUltimateTargetObject(proxiedExternalRankingService)
    }

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
}
