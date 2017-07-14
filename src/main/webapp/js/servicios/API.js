
module.exports  = {
    API_URL : 'http://localhost:3000/api/items',
    obtenerItems: function () {
        return fetch(this.API_URL)
            .then(function(response) {
                if (response.ok)
                    return response.json()
            })
    },
    addItem: function (item) {
        return fetch(this.API_URL, {
                   method: 'POST',
                   headers: {
                       'Content-type':'application/json'
                   },
                   body: JSON.stringify(item)
               }).then(function (respuesta) {
                    if (respuesta.ok)
                    {
                      return respuesta.json()
                    }
                    else
                    {
                      return {error:'token expirado'}
                    }
               })
    },
    getSignificado: function (pal) {
        return fetch('http://localhost:8080/api/significado2?palabra='+pal)
            .then(function(response) {
                if (response.ok)
                    return response.json()
            })
    },
    convertirTexto: function (texto) {
      return fetch('http://localhost:8080/api/convertirTexto', {
                   method: 'POST',
                   headers: {
                       'Content-type':'application/json'
                   },
                   body: texto
               }).then(function (respuesta) {
                    if (respuesta.ok)
                    {
                      return respuesta.json()
                    }
                    else
                    {
                      return {error:'No se ha podido convertir'}
                    }
               })
    },
     convertirTexto2: function (texto, punt, sin, pas, comp) {
      return fetch('http://localhost:8080/api/convertirTexto2?puntuacion='+punt+'&sinonimos='+sin+'&pasivas='+pas+'&complejidad='+comp, {
                   method: 'POST',
                   headers: {
                       'Content-type':'application/json'
                   },
                   body: texto
               }).then(function (respuesta) {
                    if (respuesta.ok)
                    {
                      return respuesta.json()
                    }
                    else
                    {

                      return {error:'El texto introducido no es válido, no se ha podido convertir'}
                    }
               })
    },
    calcularComplejidad: function (texto) {
      return fetch('http://localhost:8080/api/calcularComplejidad', {
                   method: 'POST',
                   headers: {
                       'Content-type':'application/json'
                   },
                   body: texto
               }).then(function (respuesta) {
                   if (respuesta.ok)
                      return respuesta.text()
               })
    },
    calcularContextos: function (texto) {
      return fetch('http://localhost:8080/api/calcularContextos', {
                   method: 'POST',
                   headers: {
                       'Content-type':'application/json'
                   },
                   body: texto
               }).then(function (respuesta) {
                   if (respuesta.ok)
                      return respuesta.json()
               })
    },
    calcularContextosModificado: function (palabras) {
      return fetch('http://localhost:8080/api/calcularContextosModificado', {
                   method: 'POST',
                   headers: {
                       'Content-type':'application/json'
                   },
                   body: JSON.stringify(palabras)
               }).then(function (respuesta) {
                   if (respuesta.ok)
                      return respuesta.json()
               })
    },
    calcularComplejidadModificado: function (palabras) {
      return fetch('http://localhost:8080/api/calcularComplejidadModificado', {
                   method: 'POST',
                   headers: {
                       'Content-type':'application/json'
                   },
                   body: JSON.stringify(palabras)
               }).then(function (respuesta) {
                   if (respuesta.ok)
                      return respuesta.text()
               })
    }


}