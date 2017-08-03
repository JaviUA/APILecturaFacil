var React = require('react')
var ReactDOM = require('react-dom')

var Calculos = require('./Calculos')
var Opciones = require('./Opciones')
var Texto = require('./Texto')




ReactDOM.render(<Opciones/>,
    document.getElementById('componenteOpciones'))
ReactDOM.render(<Texto/>,
    document.getElementById('componenteTexto'))
ReactDOM.render(<Calculos/>,
    document.getElementById('componenteCalculos'))
