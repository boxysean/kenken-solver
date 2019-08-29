import React from 'react';
import './App.css';
import Board from './Board';
import Modal from './Modal';

import update from 'immutability-helper';

class App extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      selected: new Set(),
      selecting: false,
      constraints: {},
    };
  }

  popUp(selected) {
    this.setState({
      modal: true,
    })
  }

  processRelease(cellIndex) {
    console.log("PROCESS RELEASE", this.state.modal, this.state.selecting, this.state.selected);
    if (this.state.modal) {
      console.log("SELECTING MODAL");
    } else if (this.state.selecting) {
      console.log("SELECTING RELEASE");
      this.popUp(this.state.selected);
      //
      // this.setState({
      //   selected: new Set(),
      //   selecting: false,
      // });
    }
  }

  processHover(cellIndex) {
    console.log("PROCESS HOVER", this.state.selected);
    if (this.state.selecting) {
      this.setState({
        selected: this.state.selected.add(cellIndex)
      });
    }
  }

  processBegin(cellIndex) {
    console.log("PROCESS BEGIN", this.state.selected);
    this.setState({
      selected: new Set([cellIndex]),
      selecting: true,
    });
  }

  processModal(value, operator) {
    console.log("PROCESS MODAL", value, operator, this.state.selected);

    var result = Array.from(this.state.selected).reduce(function(obj, x) {
      obj[x] = {$set: value + operator};
      return obj;
    }, {});

    console.log("RESULT");
    console.log(result);

    this.setState({
      modal: false,
      selected: new Set(),
      selecting: false,
      constraints: update(this.state.constraints, result),
    });
  }

  closeModal(event) {
    this.setState({
      modal: false,
      selected: new Set(),
      selecting: false,
    });
  }

  render() {
    return (
      <div className="App" onMouseUp={(event) => this.processRelease(event)}>
        {this.state.modal && <Modal processModal={this.processModal.bind(this)} closeModal={this.closeModal.bind(this)}></Modal>}
        <Board
          size="5"
          processHover={this.processHover.bind(this)}
          processBegin={this.processBegin.bind(this)}
          selectedCells={this.state.selected}
          constraints={this.state.constraints}
        ></Board>
      </div>
    );
  }
}

export default App;
