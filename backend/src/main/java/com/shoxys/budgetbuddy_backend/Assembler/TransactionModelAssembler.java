package com.shoxys.budgetbuddy_backend.Assembler;

import com.shoxys.budgetbuddy_backend.Controllers.TransactionController;
import com.shoxys.budgetbuddy_backend.Entities.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Assembles Transaction entities into HATEOAS EntityModel with relevant links.
 */
@Component
public class TransactionModelAssembler implements RepresentationModelAssembler<Transaction, EntityModel<Transaction>> {
    private static final Logger logger = LoggerFactory.getLogger(TransactionModelAssembler.class);

    /**
     * Converts a Transaction entity into an EntityModel with HATEOAS links.
     *
     * @param transaction the transaction entity
     * @return the EntityModel with self and collection links
     */
    @Override
    public EntityModel<Transaction> toModel(Transaction transaction) {
        logger.debug("Assembling HATEOAS model for transaction ID: {}", transaction.getId());
        EntityModel<Transaction> model = EntityModel.of(transaction,
                // Self link
                linkTo(methodOn(TransactionController.class)
                        .getTransactionById(null, transaction.getId())).withSelfRel(),
                // Collection link with default pagination parameters
                linkTo(methodOn(TransactionController.class)
                        .getTransactionsPaginated(null, 0, 20, "date,desc", null)).withRel("transactions"));
        logger.debug("Assembled HATEOAS model for transaction ID: {}", transaction.getId());
        return model;
    }
}