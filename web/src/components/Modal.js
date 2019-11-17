import React from 'react';
import Button from 'react-pushy-buttons';

import './Modal.css';

class Modal extends React.Component {
  handleChange({ target }) {
    this.setState({
      value: target.value,
    });
  }

  handleButton(event, operator) {
    this.props.processModal(this.state.value, operator);

    this.setState({
      value: null,
    });
  }

  close(event) {
    this.props.closeModal();
  }

  componentDidMount() {
    document.addEventListener("keydown", this.quickHandleInput.bind(this), false);
  }

  quickHandleInput(event) {
    if ((['+', '-', 'x', '/']).indexOf(event.key) >= 0) {
      this.handleButton(event, event.key);
    }
  }

  render() {
    return (
      <div className="Modal ModalOn">
        <div className="ModalContent">
          <span onClick={(event) => this.close(event)} className="Close">&times;</span>
          <div>
            <input
              className="ModalInput"
              autoFocus
              type="number"
              onChange={event => this.handleChange(event)}>
            </input>
          </div>
          <div className="ModalButtons">
            <Button onClick={(event) => this.handleButton(event, "+")}>+</Button>
            <Button onClick={(event) => this.handleButton(event, "-")}>-</Button>
            <Button onClick={(event) => this.handleButton(event, "x")}>x</Button>
            <Button onClick={(event) => this.handleButton(event, "/")}>/</Button>
            <Button onClick={(event) => this.handleButton(event, "")}>&nbsp;</Button>
          </div>
        </div>
      </div>
    );
  }
}

export default Modal;
