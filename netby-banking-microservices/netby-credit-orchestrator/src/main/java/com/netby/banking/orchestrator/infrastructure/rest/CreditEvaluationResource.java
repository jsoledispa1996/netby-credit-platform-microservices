package com.netby.banking.orchestrator.infrastructure.rest;

import com.netby.banking.orchestrator.application.service.ICreditEvaluationService;
import com.netby.banking.orchestrator.infrastructure.rest.dto.CreditEvaluationRequest;
import com.netby.banking.orchestrator.infrastructure.rest.dto.CreditEvaluationResponse;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/v1/credit-evaluations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CreditEvaluationResource {

    @Inject
    ICreditEvaluationService evaluationService;

    @POST
    public Response evaluate(@Valid CreditEvaluationRequest request) {
        CreditEvaluationResponse result = evaluationService.evaluate(request);
        return Response.status(Response.Status.CREATED).entity(result).build();
    }

    @GET
    public List<CreditEvaluationResponse> listAll() {
        return evaluationService.findAll();
    }
}
