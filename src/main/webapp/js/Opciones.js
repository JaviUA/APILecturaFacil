var React = require('react')
var API_lista = require('./servicios/API')
var EventBus = require('./servicios/EventBus')



module.exports = React.createClass({
     

    componentDidMount: function () {
      EventBus.eventEmitter.addListener('cambiaOpcionesModificado', this.activaModificado)
      EventBus.eventEmitter.addListener('cambiaOpcionesInicial', this.desactivaModificado)
    },
    getInitialState : function () {
      return {pasivas: true, sinonimos: true, puntuacion: true, complejidad: false, modificado: false, complejidadAntes: undefined, complejidadDespues: undefined}
    },
    activaModificado: function (complejidad) {
        
        if(complejidad.calculada==true)
        {
          this.setState({complejidadAntes: complejidad.antes, complejidadDespues: complejidad.despues})
        }
       
     this.setState({modificado: true})

    },
    desactivaModificado: function () {
        
     
       
     this.setState({modificado: false})

    },
    clickPasivas: function () {
      var cambio=!this.state.pasivas;
      EventBus.eventEmitter.emitEvent('checkPasivas', [cambio])
     this.setState({pasivas: cambio})

    },
    clickSinonimos: function () {
      var cambio=!this.state.sinonimos;
      EventBus.eventEmitter.emitEvent('checkSinonimos', [cambio])
     this.setState({sinonimos: cambio})

    },
    clickPuntuacion: function () {
      var cambio=!this.state.puntuacion;
      EventBus.eventEmitter.emitEvent('checkPuntuacion', [cambio])
     this.setState({puntuacion: cambio})

    },
    clickComplejidad: function () {
      var cambio=!this.state.complejidad;
      EventBus.eventEmitter.emitEvent('checkComplejidad', [cambio])
     this.setState({complejidad: cambio})

    },
    render: function () {
      var divStyle={'font-size': '15px', 'text-align' : 'center'};
      if(this.state.modificado==false)
      {
        return <div id="opciones" style={divStyle}>
                  <h1>Opciones</h1>
                  <ul className="list-group">
                  <li className="list-group-item"> <input type="checkbox"   checked={this.state.pasivas} onClick={this.clickPasivas}/>&nbsp;
                   <label>  Pasivas -> Activas </label>  </li>
                   <li className="list-group-item"><input type="checkbox"   checked={this.state.sinonimos} onClick={this.clickSinonimos} />&nbsp;
                   <label>  Sustituir sinónimos </label></li>
                   <li className="list-group-item"><input type="checkbox"   checked={this.state.puntuacion} onClick={this.clickPuntuacion}/>&nbsp;
                   <label>  Cambiar puntuación </label> </li>
                   <li className="list-group-item"><input type="checkbox"   checked={this.state.complejidad} onClick={this.clickComplejidad}/>&nbsp;
                   <label>  Calcular complejidad </label> </li>
                   </ul>

               </div>


      }
      else
      {
         return <div id="opciones" style={divStyle}>
                  <h1>Opciones utilizadas</h1>
                  <ul className="list-group">
                   <label hidden= {!this.state.pasivas}>  <li className="list-group-item">Pasivas -> Activas</li> </label>
                   <label hidden={!this.state.sinonimos}>  <li className="list-group-item">Sustituir sinónimos</li> </label>
                  <label hidden={!this.state.puntuacion}>  <li className="list-group-item">Cambiar puntuación</li> </label>
                   <label  hidden={!this.state.complejidad}><li className="list-group-item">  Calcular complejidad</li> </label>
                   </ul>
                   <div  hidden={!this.state.complejidad}>  <u><b>Resultados</b></u> <br/>
                   Índice de lecturabilidad <br/>
                    Antes: {this.state.complejidadAntes} <br/>
                    Despues: {this.state.complejidadDespues} </div>
               </div>
      }
    }
})