import React from 'react';
import _ from 'lodash';
import { Spinner } from "react-loading-io";

import Cell from './Cell';
import './Board.css';

class Board extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      showTooltip: true,
    }
  }

  getCells(selectedCells) {
    var cells = [];

    var constraintsUsed = new Set();

    for (var i = 0; i < this.props.size; i++) {
      for (var j = 0; j < this.props.size; j++) {
        var cellIndex = i * this.props.size + j;

        var c = this.props.constraints[cellIndex];

        var borders = {
          left: (cellIndex % this.props.size) - 1 < 0 || (!_.isEqual(this.props.constraints[cellIndex - 1], c)),
          right: (cellIndex % this.props.size) + 1 >= this.props.size || (!_.isEqual(this.props.constraints[cellIndex + 1], c)),
          top: cellIndex - this.props.size < 0 || (!_.isEqual(this.props.constraints[cellIndex - this.props.size], c)),
          bottom: cellIndex + this.props.size >= this.props.size * this.props.size || (!_.isEqual(this.props.constraints[cellIndex + this.props.size], c)),
        };

        var cellDisplay;

        if (this.props.answers[cellIndex]) {
          cellDisplay = this.props.answers[cellIndex];
        } else if (c && !constraintsUsed.has(c.name)) {
          cellDisplay = c.formula;
          constraintsUsed.add(c.name);
        } else {
          cellDisplay = '';
        }

        const element = <Cell
          cellIndex={cellIndex}
          processBegin={this.props.processBegin}
          processHover={this.props.processHover}
          isSelected={selectedCells.has(cellIndex)}
          constraint={c}
          display={cellDisplay}
          borders={borders}
        ></Cell>;
        cells.push(element);
      }
    }

    return cells;
  }

  touchTooltip() {
    this.setState({
      showTooltip: false,
    });
  }

  render() {
    var boardContainerStyle = {
      'width': this.props.size * 64 + 4,
      'height': this.props.size * 64 + 4,
    };

    var gridColumnTemplateStyle = {
      'gridTemplateColumns': `repeat(${this.props.size}, 60px)`
    };

    var tooltipDisplayStyle = {};

    if (!this.state.showTooltip) {
      tooltipDisplayStyle['display'] = 'none';
    }

    var loadingDisplayStyle = {};

    if (!this.props.isSolving) {
      loadingDisplayStyle['display'] = 'none';
    }

    return (
      <div className="BoardContainer" style={boardContainerStyle}>
        <div className="Board" style={gridColumnTemplateStyle}>
          {this.getCells(this.props.selectedCells).map(cell => cell)}
        </div>

        <div
          onMouseDown={(event) => this.touchTooltip()}
          onTouchStart={(event) => this.touchTooltip()}
          className="BoardTooltipContainer"
          style={tooltipDisplayStyle}
        >
          <div className="BoardTooltip">
            <p>{this.props.tooltip}</p>
          </div>
        </div>

        <div className="BoardLoading" style={loadingDisplayStyle}>
          <Spinner color="#4CA7FD" size={100} />
        </div>
      </div>
    );
  }
}

export default Board;
