var React = require('react')
var API_lista = require('./servicios/API')
var EventBus = require('./servicios/EventBus')
var Contexto = require('./Contexto')


module.exports = React.createClass({
    componentDidMount: function () {
      EventBus.eventEmitter.addListener('modificaComplejidad', this.modificaComplejidad)
      EventBus.eventEmitter.addListener('modificaContextos', this.modificaContextos)
    },
    getInitialState : function () {
      return {complejidad: undefined, contextos:[]}
    },
    modificaComplejidad : function (puntos) {
     this.setState({complejidad:puntos})
    },
    modificaContextos: function (resultado) {
     
      alert(resultado);
     this.setState({contextos:resultado})
    },
    clickContextos : function () {
      
        EventBus.eventEmitter.emitEvent('calculaContextos')
    },
    clickComplejidad : function () {
      
        EventBus.eventEmitter.emitEvent('calculaComplejidad')
    },
    render: function () {
       var estilo={'width':'80%',
                    'text-align':"center"};

                    

      var max=5;
      if(this.state.contextos.length<5)
      {
        max=this.state.contextos.length;
      }

       var conts =[]
       for (var j=0; j<max ; j++) {
          
            var actual = this.state.contextos[j]
                        var elemento = <Contexto
                                 nom={actual.nombre}
                                 val={actual.valor}/>
            conts.push(elemento)
        }


      if(this.state.complejidad!=undefined)
      {
        return <div style={estilo}>
                  <h1>Cálculos</h1>
                  <button className="btn btn-primary" style={estilo} onClick={this.clickComplejidad}  >Calcular complejidad</button> <br/> <br/>
                  <label for="complejidad">Índice de lecturabilidad: {this.state.complejidad}</label> <br/> <br/>
                  <button className="btn btn-primary" style={estilo} onClick={this.clickContextos}  >Calcular contextos</button> <br/><br/>
                   <ul className="list-group">
                  {conts}
                  </ul>
               </div>
      }
      else
      {
        return <div style={estilo}>
                  <h1>Cálculos</h1>
                  <button className="btn btn-primary" style={estilo} onClick={this.clickComplejidad}  >Calcular complejidad</button> <br/> <br/>
                   <button className="btn btn-primary" style={estilo} onClick={this.clickContextos}  >Calcular contextos</button> <br/><br/>
                    <ul className="list-group">
                   {conts}
                   </ul>
               </div>
      }


      


    }
})