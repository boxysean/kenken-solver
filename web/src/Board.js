import React from 'react';

import Cell from './Cell';
import './Board.css';

class Board extends React.Component {
  onMouseDown(event) {
    console.log("DOWN");
    console.log(event);
  }

  onMouseUp(event) {
    console.log("UP");
    console.log(event);
    this.props.onRelease(event);
  }

  getCells(selectedCells) {
    var cells = [];

    for (var i = 0; i < this.props.size; i++) {
      for (var j = 0; j < this.props.size; j++) {
        var cellIndex = i * this.props.size + j;
        const element = <Cell
          cellIndex={cellIndex}
          processRelease={(event) => this.props.processRelease(event)}
          processBegin={(event) => this.props.processBegin(event)}
          processHover={(event) => this.props.processHover(event)}
          isSelected={selectedCells.has(cellIndex)}
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
        // onMouseDown={(event) => this.onMouseDown(event)}
        // onMouseUp={(event) => this.onMouseUp(event)}
      >
        {this.getCells(this.props.selectedCells).map(cell => cell)}
      </div>
    );
  }
}

export default Board;
