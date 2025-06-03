export ${packageName}.entity.${entityName}
    -> package ${packageName}.entity.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

${entityName}ListDTO {
    #allScalars
}

${entityName}DetailDTO {
    #allScalars
}

input ${entityName}CreateDTO {
    #allScalars(this)
}

input ${entityName}UpdateDTO {
    #allScalars(this)
    id!
}

specification ${entityName}Specification {
    #allScalars
}
