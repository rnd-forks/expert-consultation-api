package com.code4ro.legalconsultation.controller;

import com.code4ro.legalconsultation.model.dto.DocumentViewDto;
import com.code4ro.legalconsultation.model.persistence.DocumentConsolidated;
import com.code4ro.legalconsultation.model.persistence.DocumentMetadata;
import com.code4ro.legalconsultation.service.api.DocumentService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/document")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @ApiOperation(value = "Return document metadata for all documents in the platform",
            response = List.class,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("")
    public ResponseEntity<Page<DocumentMetadata>> getAllDocuments(@ApiParam("Page object information being requested") Pageable pageable) {
        Page<DocumentMetadata> documents = documentService.fetchAll(pageable);

        return new ResponseEntity<>(documents, HttpStatus.OK);
    }

    @ApiOperation(value = "Return document metadata for a single document in the platform based on id",
            response = DocumentMetadata.class,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/{id}")
    public ResponseEntity getDocumentById(@ApiParam("Id of the document object being requested") @PathVariable UUID id) {
        return ResponseEntity.ok(documentService.fetchOne(id));
    }

    @ApiOperation(value = "Return metadata and content for a single document in the platform based on id",
            response = DocumentConsolidated.class,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/{id}/consolidated")
    public ResponseEntity getDocumentConsolidatedById(@ApiParam("Id of the document object being requested") @PathVariable UUID id) {
        return ResponseEntity.ok(documentService.fetchOneConsolidated(id));
    }

    @ApiOperation(value = "Delete metadata and contents for a single document in the platform based on id")
    @DeleteMapping("/{id}")
    public ResponseEntity deleteDocument(@ApiParam("Id of the document object being deleted") @PathVariable UUID id) {
        documentService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Create a new document in the platform",
            response = UUID.class,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("")
    public ResponseEntity<UUID> createDocument(
            @Valid @RequestBody DocumentViewDto documentViewDto) {
        DocumentConsolidated consolidated = documentService.create(documentViewDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(consolidated.getId());
    }

    @ApiOperation(value = "Modify a saved document in the platform",
            response = UUID.class,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PutMapping("/{id}")
    public ResponseEntity<UUID> modifyDocument(@ApiParam(value = "Id of the document being modified") @PathVariable("id") UUID id,
                                               @Valid @RequestBody DocumentViewDto documentViewDto) {
        DocumentConsolidated consolidated = documentService.update(id, documentViewDto);
        return ResponseEntity.ok(consolidated.getId());
    }
}
