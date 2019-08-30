import React from 'react';

import Cell from './Cell';
import './Board.css';

class Board extends React.Component {
  getCells(selectedCells) {
    var cells = [];

    for (var i = 0; i < this.props.size; i++) {
      for (var j = 0; j < this.props.size; j++) {
        var cellIndex = i * this.props.size + j;
        const element = <Cell
          cellIndex={cellIndex}
          processBegin={this.props.processBegin}
          processHover={this.props.processHover}
          isSelected={selectedCells.has(cellIndex)}
          constraint={this.props.constraints[cellIndex]}
          answer={this.props.answers[cellIndex]}
        ></Cell>;
        cells.push(element);
      }
    }

    return cells;
  }

  render() {
    var gridColumnTemplateStyle = {
      'grid-template-columns': `repeat(${this.props.size}, 40px)`
    };

    return (
      <div
        className="Board"
        style={gridColumnTemplateStyle}
      >
        {this.getCells(this.props.selectedCells).map(cell => cell)}
      </div>
    );
  }
}

export default Board;
