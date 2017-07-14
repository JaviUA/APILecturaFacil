var React = require('react')
var ReactDOM = require('react-dom')

var Calculos = require('./Calculos')
var Opciones = require('./Opciones')
var NuevoItem = require('./NuevoItem')




ReactDOM.render(<Opciones/>,
    document.getElementById('componenteOpciones'))
ReactDOM.render(<NuevoItem/>,
    document.getElementById('componenteNuevoItem'))
ReactDOM.render(<Calculos/>,
    document.getElementById('componenteCalculos'))
