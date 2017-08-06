
module.exports  = {
    API_URL : 'https://applecturafacilrest.herokuapp.com/api',
    getSignificado: function (pal) {
        return fetch(this.API_URL+'/significado2?palabra='+pal)
            .then(function(response) {
                if (response.ok)
                    return response.json()
            })
    },
    convertirTexto: function (texto) {
      return fetch(this.API_URL+'/convertirTexto', {
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
      return fetch(this.API_URL+'/convertirTexto2?puntuacion='+punt+'&sinonimos='+sin+'&pasivas='+pas+'&complejidad='+comp, {
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
      return fetch(this.API_URL+'/calcularComplejidad', {
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
      return fetch(this.API_URL+'/calcularContextos', {
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
      return fetch(this.API_URL+'/calcularContextosModificado', {
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
      return fetch(this.API_URL+'/calcularComplejidadModificado', {
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
