package space.rodionov.financialsobriety.data

import javax.inject.Inject


class FinRepository @Inject constructor(
    private val finDb: FinDatabase
) {
    private val finDao = finDb.finDao()


}