# jav-eas-patterns-quotation-mngr

### Pontificia Universidad Javeriana. Bogotá.

Manager para administración de cotizaciones (Objeto de negocio) para al trabajo "socialización II" de la asignatura patrones 
de diseño, en la Especialización de Arquitectura de Software Empresarial 2020 I.

### Integrantes:

* Andres Martinez Cobos
* Fabian Acero
* Robinson Torres

* * *

### Recursos:

<table>
    <tr>
        <td>PATH</td>
        <td>DESCRIPCIÓN</td>
        <td>VERBO</td>
        <td>HTTP CODE OK</td>
        <td>HTTP CODES FAILED</td>
    </tr>
    <tr>
        <td>/request-quotation/{filter}</td>
        <td>Recupera el listado de solicitudes de cotizacion por un filtro dado. Validdos:<br>
            CATEGORY,
            ID,
            PERSON,
            PERSON_CATEGORY,
            PERSON_STATUS
        </td>
        <td>GET</td>
        <td>200 - OK -</td>
        <td>400 - BAD_REQUEST - El filtro contiene valores incorrectos</td>
    </tr>
    <tr>
        <td>/request-quotation</td>
        <td>Recupera el listado de productos o servicios por una categoria dada</td>
        <td>POST</td>
        <td>201 - CREATED -</td>
        <td>406 - NOT_ACCEPTABLE - Datos de creación invalidos</td>
    </tr>
</table>