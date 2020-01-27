package com.thinktag.fuzzdata.controller;

import com.thinktag.fuzzdata.service.CodeGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FuzzyDataController {

    @Autowired
    CodeGenerationService codeGenerationService;

    @PostMapping("api/genSchemaCode")
    void postSchemaCode(@RequestParam(value = "root") String root,
                        @RequestBody String schema) throws Exception {
        codeGenerationService.generateCode(schema, root);
    }


    @GetMapping("api/dummyData")
    @ResponseBody
    String dummyData(@RequestParam(value = "root") String root) throws Exception {
        return codeGenerationService.generateObject(root);
    }
}
