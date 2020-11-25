package com.ebarti.openapi.codegen;

import org.openapitools.codegen.languages.AbstractGoCodegen;
import org.openapitools.codegen.CliOption;
import org.openapitools.codegen.CodegenConstants;
import org.openapitools.codegen.CodegenOperation;
import org.openapitools.codegen.CodegenType;
import org.openapitools.codegen.SupportingFile;
import org.openapitools.codegen.meta.features.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

public class ExtendedGoServerGenerator extends AbstractGoCodegen {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExtendedGoServerGenerator.class);

  // source folder where to write the files
  protected int serverPort = 8080;
  protected String apiVersion = "1.0.0";
  protected String initialPackageName = "github.com" + File.separator + "ebarti" + File.separator + "extendedgingoserver";


  public ExtendedGoServerGenerator() {
    super();

    modifyFeatureSet(features -> features
                .includeDocumentationFeatures(DocumentationFeature.Readme)
                .wireFormatFeatures(EnumSet.of(WireFormatFeature.JSON, WireFormatFeature.XML))
                .securityFeatures(EnumSet.noneOf(
                        SecurityFeature.class
                ))
                .excludeGlobalFeatures(
                        GlobalFeature.XMLStructureDefinitions,
                        GlobalFeature.Callbacks,
                        GlobalFeature.LinkObjects,
                        GlobalFeature.ParameterStyling
                )
                .excludeSchemaSupportFeatures(
                        SchemaSupportFeature.Polymorphism
                )
                .excludeParameterFeatures(
                        ParameterFeature.Cookie
                )
        );

    // set the output folder here
    outputFolder = "generated-code/extended-gin-go-server";

    /**
     * Models.  You can write model files using the modelTemplateFiles map.
     * if you want to create one template for file, you can do so here.
     * for multiple files for model, just put another entry in the `modelTemplateFiles` with
     * a different extension
     */
    modelTemplateFiles.put(
      "model.mustache", // the template to use
      ".sample");       // the extension for each file to write

    /**
     * Api classes.  You can write classes for each Api file with the apiTemplateFiles map.
     * as with models, add multiple entries with different extensions for multiple files per
     * class
     */
    apiTemplateFiles.put(
                "controller-api.mustache",   // the template to use
                ".go");       // the extension for each file to write

    /**
     * Template Location.  This is the location which templates will be read from.  The generator
     * will use the resource stream to attempt to read the templates.
     */
    embeddedTemplateDir = templateDir = "extended-gin-go-server";

    /**
     * Reserved words.  Override this with reserved words specific to your language
     */
    setReservedWordsLowerCase(
            Arrays.asList(
                        // data type
                        "string", "bool", "uint", "uint8", "uint16", "uint32", "uint64",
                        "int", "int8", "int16", "int32", "int64", "float32", "float64",
                        "complex64", "complex128", "rune", "byte", "uintptr",

                        "break", "default", "func", "interface", "select",
                        "case", "defer", "go", "map", "struct",
                        "chan", "else", "goto", "package", "switch",
                        "const", "fallthrough", "if", "range", "type",
                        "continue", "for", "import", "return", "var", "error", "nil")
                // Added "error" as it's used so frequently that it may as well be a keyword
        );


    CliOption optServerPort = new CliOption("serverPort", "The network port the generated server binds to");
    optServerPort.setType("int");
    optServerPort.defaultValue(Integer.toString(serverPort));
    cliOptions.add(optServerPort);
  }
  @Override
    public Map<String, Object> postProcessOperationsWithModels(Map<String, Object> objs, List<Object> allModels) {
        objs = super.postProcessOperationsWithModels(objs, allModels);

        Map<String, Object> operations = (Map<String, Object>) objs.get("operations");
        List<CodegenOperation> operationList = (List<CodegenOperation>) operations.get("operation");
        for (CodegenOperation op : operationList) {
            if (op.path != null) {
                op.path = op.path.replaceAll("\\{(.*?)\\}", ":$1");
            }
        }
        return objs;
    }

    @Override
    public void processOpts() {
        super.processOpts();

        if (additionalProperties.containsKey(CodegenConstants.PACKAGE_NAME)) {
            setPackageName((String) additionalProperties.get(CodegenConstants.PACKAGE_NAME));
        } else {
            setPackageName(initialPackageName);
            additionalProperties.put(CodegenConstants.PACKAGE_NAME, this.packageName);
        }

        /*
         * Additional Properties.  These values can be passed to the templates and
         * are available in models, apis, and supporting files
         */
        if (additionalProperties.containsKey("apiVersion")) {
            this.apiVersion = (String)additionalProperties.get("apiVersion");
        } else {
            additionalProperties.put("apiVersion", apiVersion);
        }

        if (additionalProperties.containsKey("serverPort")) {
            this.serverPort = Integer.parseInt((String)additionalProperties.get("serverPort"));
        } else {
            additionalProperties.put("serverPort", serverPort);
        }

        modelPackage = packageName;
        apiPackage = packageName;

        /*
         * Supporting Files.  You can write single files for the generator with the
         * entire object tree available.  If the input file has a suffix of `.mustache
         * it will be processed by the template engine.  Otherwise, it will be copied
         */
        supportingFiles.add(new SupportingFile("openapi.mustache", "api", "openapi.yaml"));
        supportingFiles.add(new SupportingFile("main.mustache", "", "main.go"));
        supportingFiles.add(new SupportingFile("Dockerfile.mustache", "", "Dockerfile"));
        supportingFiles.add(new SupportingFile("routers.mustache", packageName, "routers.go"));
        supportingFiles.add(new SupportingFile("go.mod.mustache", "", "go.mod"));
        supportingFiles.add(new SupportingFile("logger.mustache", packageName, "logger.go"));
        supportingFiles.add(new SupportingFile("impl.mustache",packageName, "impl.go"));
        supportingFiles.add(new SupportingFile("helpers.mustache", packageName, "helpers.go"));
        supportingFiles.add(new SupportingFile("api.mustache", packageName, "api.go"));
        supportingFiles.add(new SupportingFile("README.mustache", "", "README.md")
                .doNotOverwrite());
    }

    @Override
    public String apiPackage() {
        return packageName;
    }


  /**
   * Configures the type of generator.
   *
   * @return  the CodegenType for this generator
   * @see     org.openapitools.codegen.CodegenType
   */
  public CodegenType getTag() {
    return CodegenType.SERVER;
  }

  /**
   * Configures a friendly name for the generator.  This will be used by the generator
   * to select the library with the -g flag.
   *
   * @return the friendly name for the generator
   */
  public String getName() {
    return "extended-gin-go-server";
  }


  /**
   * Returns human-friendly help for the generator.  Provide the consumer with help
   * tips, parameters here
   *
   * @return A string value for the help message
   */
  public String getHelp() {
    return "Generates an extended gin-go-server server library.";
  }

    /**
   * Location to write model files.  You can use the modelPackage() as defined when the class is
   * instantiated
   */
  public String modelFileFolder() {
        return outputFolder + File.separator + apiPackage();
    }

  /**
   * Location to write api files.  You can use the apiPackage() as defined when the class is
   * instantiated
   */
  @Override
  public String apiFileFolder() {
        return outputFolder + File.separator + apiPackage();
    }

}