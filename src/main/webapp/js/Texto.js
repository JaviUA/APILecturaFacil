var React = require('react')
var API = require('./servicios/API')
var EventBus = require('./servicios/EventBus')
var Palabra = require('./Palabra')
var Significado = require('./Significado')
var Loader = require('react-loader');



var TextoComponente = React.createClass({
   componentDidMount: function () {
     EventBus.eventEmitter.addListener('nuevoSignificado', this.addSignificado)

     EventBus.eventEmitter.addListener('checkPasivas', this.cambiaPasivas)
     EventBus.eventEmitter.addListener('checkSinonimos', this.cambiaSinonimos)
     EventBus.eventEmitter.addListener('checkPuntuacion', this.cambiaPuntuacion)
     EventBus.eventEmitter.addListener('checkComplejidad', this.cambiaComplejidad)

     EventBus.eventEmitter.addListener('calculaComplejidad', this.calculaComplejidad)
     EventBus.eventEmitter.addListener('calculaContextos', this.calculaContextos)
     
    },
    getInitialState : function () {
      return {palabras:[], significados:[], modificado: false, 
      pasivas:true,sinonimos:true,puntuacion:true,complejidad:false, error:undefined, estilo:{'font-size': '100%'}, loading: false}
    },
    calculaComplejidad: function() {
      if(this.state.modificado==true)
      {
         API.calcularComplejidadModificado(this.state.palabras).then(function(resultado) {

        EventBus.eventEmitter.emitEvent('modificaComplejidad', [resultado])
        }.bind(this));
      }
      else if (this.campoTexto.value!="")
      {
        API.calcularComplejidad(this.campoTexto.value).then(function(resultado) {
        this.setState({error: undefined})

        EventBus.eventEmitter.emitEvent('modificaComplejidad', [resultado])
        }.bind(this));

      } 
      else
      {
        this.setState({error:"No se ha podido calcular, introduzca un texto"})
      }
    },
    calculaContextos: function() {
      if(this.state.modificado==true)
      {
         API.calcularContextosModificado(this.state.palabras).then(function(resultado) {

        EventBus.eventEmitter.emitEvent('modificaContextos', [resultado])
        }.bind(this));
      }
      else if (this.campoTexto.value!="")
      {
        API.calcularContextos(this.campoTexto.value).then(function(resultado) {
        this.setState({error: undefined})

        EventBus.eventEmitter.emitEvent('modificaContextos', [resultado])
        }.bind(this));

      } 
      else
      {
        this.setState({error:"No se han podido calcular los contextos, introduzca un texto"})
      }
    },
    cambiaPasivas: function(estado) {
      this.setState({pasivas: estado})
    },
    cambiaSinonimos: function(estado) {
      this.setState({sinonimos: estado})
    },
    cambiaPuntuacion: function(estado) {
      this.setState({puntuacion: estado})
    },
    cambiaComplejidad: function(estado) {
      this.setState({complejidad: estado})
    },
    addSignificado: function(nuevos) {
      this.setState({significados: nuevos})
    },
    encogeDiv: function() {
      this.setState({estilo:{'font-size': '75%'}})
    },
    normalizaDiv: function() {
      this.setState({estilo:{'font-size': '100%'}})
    },
    agrandaDiv: function() {
      this.setState({estilo:{'font-size': '125%'}})
    },
    clickAdd: function () {
 

this.setState({loading:true})
 var pas=this.state.pasivas;
 var sin=this.state.sinonimos;
 var punt=this.state.puntuacion;
 var comp=this.state.complejidad;
   API.convertirTexto2(this.campoTexto.value, punt, sin, pas, comp).then(function(resultado) {

        if(resultado.error==undefined)
        {

        this.setState({palabras:resultado.palabras, modificado:true})
        EventBus.eventEmitter.emitEvent('cambiaOpcionesModificado', [resultado.complejidad]);
        }
        else
        {
          this.setState({error:resultado.error, loading:false})
        }

    }.bind(this));


    },
    render: function () {
     
     var derecha={'text-align':'right'};

     var estiloBoton={'width':'30%'};

     var centerSection= {
      'text-align': 'center', 
     }

      var small={'font-size': '75%',
                 'margin-right': '5px'};
      var medium={'font-size': '100%',
                 'margin-right': '5px'};
      var big={'font-size': '125%',
                'margin-right': '5px'};

      var signs =[]
       for (var j=0; j<this.state.significados.length; j++) {
            var actual = this.state.significados[j]
            var elemento = <Significado
                                 texto={actual.texto}
                                 contexto={actual.contexto}/>
            signs.push(elemento)
        }

      var prods = []
        for (var i=0; i<this.state.palabras.length; i++) {
            var actual = this.state.palabras[i]
            var elemento = <Palabra 
                                 nombre={actual.palabra}
                                 tipo={actual.tipo}/>
            prods.push(elemento)
        }

      if(this.state.modificado==false)
      {
        
        return <div>

              <div hidden={this.state.loading}>
            <h1>Tu texto:</h1>

            <textarea placeholder="Introduce el texto..."
                      ref={(campo)=>{this.campoTexto=campo}}/> <br/>
            <div style={centerSection}> <button className="btn btn-primary" style={estiloBoton} onClick={this.clickAdd}  >Convertir</button> <br/>
          <span className="label label-danger">{this.state.error}</span></div>
          </div>
          <div hidden={!this.state.loading}> <img src="js/loading.gif" /> </div>
             
           
        </div>
      } 
      else
      {
        return <div> 
        <div style={derecha}>
          <a style={small} href="#" onClick={this.encogeDiv}>A</a>
          <a style={medium} href="#" onClick={this.normalizaDiv}>A</a> 
          <a style={big} href="#" onClick={this.agrandaDiv}>A</a>
          </div>
                    <br/>
              <div style={this.state.estilo}>{prods} <br/> <br/>
              {signs} 
              </div>
            </div> 

      }
    }
})
module.exports = TextoComponente
