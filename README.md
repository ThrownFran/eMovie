# eMovie Code Challenge

1. ¿En qué consiste el principio de responsabilidad única? ¿Cuál es su propósito?
    
    El principio de responsabilidad única es un concepto que establece que cada clase, módulo o componente de software debe tener una función única. Dicho de otra manera, cada clase debe tener una sola razón de cambio (Definición de Robert C. Martin). 

    El propósito de este principio es que separemos nuestro software en partes desacopladas y aisladas entre sí. Para que cada parte tenga una función e intención clara y coherente a su nombre. Aplicar este principio implica en crear software robusto, escalable, flexible y legible, además de facilitar el Testing y el mantenimiento. 

    Este principio es el primero y más importante de los principios SOLID. Y es fundamental para escribir software de calidad.


2. ¿Qué características tiene, según su opinión, un “buen” código o código limpio?

    Un código limpio es un código fácil de entender y de refactorizar.

    El código limpio se escribe pensando en personas y no en el compilador. La idea es que a través de nuestro código podamos contar un cuento fácil de seguir.

    Un código limpio tiene las siguientes características:

    - Nombres significativos y descriptivos de variables, clases, métodos.

    - DRY. Evita repetir código.

    - KISS: Mantén el código simple, tan simple que cualquiera podría entenderlo. 

    - Las funciones o métodos solo deben ser cortas y tener una responsabilidad única.

    - Código autodescriptivo. Se debe entender fácilmente evitando comentarios innecesarios. 

    - Evita Code smells. Evita que el código sea difícil de cambiar, frágil, complejo o difícil de entender.

    - Bajo número de argumentos. Si hay exceso de argumentos es mejor refactorizar creando una clase que junte dichos argumentos. 

    - Las clases deben encapsular su estructura interna y seguir el principio de responsabilidad única.


3. Detalla cómo harías todo aquello que no hayas llegado a completar.

    Detalle de película.

    - Actores

    - Reseñas 

    - Imágenes de películas. 

    Detalles de futura implementación. 

    Actualmente se observa un flow GetMovieDetailUseCase que recibe actualizaciones tipo MovieDetail de la fuente de datos local. 

    El objeto MovieDetail está separado de Movie para que pueda escalar. 
    Para agregar la funcionalidad pendiente podemos agregar a través de composición en MovieDetail los elementos faltantes (Actores, Reseñas, Lista de imágenes).

    Y al entrar en el Detalle se invoca otro método RefreshMovieDetailUseCase que se encargará de solicitar al repositorio que descargue los datos de Internet y actualice la base de datos. 

    De esta manera la vista seguiría recibiendo actualizaciones del mismo objeto MovieDetail con la funcionalidad agregada. La tabla en la base de datos de actores, reseñas y lista de imágenes es necesaria para el funcionamiento offline.


