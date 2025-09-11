package ita.tech.eveniment.state

import ita.tech.eveniment.model.InformacionRecursoModel

data class DescargaRecursosState (
    val recursosPantalla: List<String> = emptyList(),
    val recursosPlantilla: List<InformacionRecursoModel> = emptyList(),
    val recursosPrincipal: List<InformacionRecursoModel> = emptyList()
)